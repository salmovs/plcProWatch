import cv2 as cv
import numpy as np
from urllib.request import urlopen
import paho.mqtt.publish as publish
import json
import time
import re
import pytesseract
from smb.SMBConnection import SMBConnection
from datetime import datetime


if __name__ == "__main__":
    pytesseract.pytesseract.tesseract-ocr = 'C:\\Program Files\\Tesseract-OCR\\tesseract.exe'
    
    # Берем картинку с камеры
    webUrl = urlopen('http://192.168.1.9:8765/picture/4/current')
    imgNp = np.array(bytearray(webUrl.read()), dtype=np.uint8)
    img = cv.imdecode(imgNp, -1)
    cv.imwrite('temp_image.jpg', img)

    # Меняем цветовую модель
    gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    ret, thresh_gray = cv.threshold(gray, 120, 255, cv.THRESH_BINARY_INV)

    # Ищем все контуры на изображении
    contours, hierarchy = cv.findContours(thresh_gray, cv.RETR_LIST, cv.CHAIN_APPROX_SIMPLE)

    # Список всех контуров с изображения
    contours_list = list()

    # Определяем лого для проверки на наличие этикетки
    logo_center = None
    logo_area = None
    luk = ''

    # Перебираем все контуры
    for cnt in contours:
        # Вписываем в контур прямоугольник
        rect = cv.minAreaRect(cnt)
        box = np.int0(cv.boxPoints(rect))

        # Параметры прямоугольника (координаты центра)
        h = int(rect[1][0])
        w = int(rect[1][1])
        x = int(rect[0][0])
        y = int(rect[0][1])

        # Вычисляем площадь прямоугольника
        area = int(h * w)
        center = (x, y)

        contours_list.append(cnt)

        # Выделяем логотип и прямоугольник из всех контуров по площади
        if (area > 27000) and (area < 35000):
            logo_center = center
            logo_area = (h, w)

        # Выделяем надпись "ЛУКОЙЛ" под логотипом
        elif (luk == '') and (logo_center is not None):
            luk_x = logo_center[0] - int(logo_area[1] / 2) - 50
            luk_y = logo_center[1] + int(logo_area[0] / 2)
            luk_img = gray[luk_y:luk_y+50, luk_x:luk_x+250]
            luk = pytesseract.image_to_string(luk_img, lang='rus')
            luk = re.findall(r'ЛУКОЙЛ', luk)

    # Количество распознанных контуров у этикетки должно быть больше 3200,
    # логотип должен быть найден
    if (len(contours_list) > 5200) and (logo_center is not None) and ('ЛУКОЙЛ' in luk):
        print('Бочка с этикеткой', len(contours_list))
        timestamp = int(time.time())

        # Распознаем название масла на вырезанной части этикетки
        name_x = logo_center[0] + logo_area[1] // 2
        name_y = logo_center[1] - logo_area[0] // 2
        name_img = gray[name_y:name_y + 250, name_x:name_x + 950]
        name_erode = cv.erode(name_img, np.ones((3, 3), np.uint8), iterations=1)
        name = pytesseract.image_to_string(name_erode, config='-l eng+kaz+rus+chi_sim --psm 6')
        name = re.findall(r'\n([a-zA-Z\d\- ,./]+)', name)
        if name == list():
            name = ''
        else:
            name = name[0]

        # Распознаем партию на вырезанной части этикетки
        pocket_order_x = logo_center[0] + 500
        pocket_order_y = logo_center[1] + 600
        pocket_order_img = gray[pocket_order_y:pocket_order_y + 200,
                           pocket_order_x:pocket_order_x + 300]
        pocket_order = pytesseract.image_to_string(pocket_order_img, config='-l eng --psm 6')
        pocket_order = re.findall(r'FO-\d{4}-\d\d\.\d', pocket_order)
        if pocket_order == list():
            pocket_order = ''
        else:
            pocket_order = pocket_order[0]

        # Распознаем товарный код на вырезанной части этикетки
        ean13_x = logo_center[0] + 860
        ean13_y = logo_center[1] + 500
        ean13_img = gray[ean13_y:ean13_y + 200, ean13_x:ean13_x + 200]
        ean13 = pytesseract.image_to_string(ean13_img, config='--psm 6')
        ean13 = re.findall(r'\d\s?\d{6}\s?\d{6}', ean13)
        if ean13 == list():
            ean13 = ''
        else:
            ean13 = ean13[0].replace(' ', '')

        # Распознаем объем на вырезанной части этикетки
        volume_x = logo_center[0] + 850
        volume_y = logo_center[1] + 670
        volume_img = gray[volume_y:volume_y + 150, volume_x:volume_x + 200]
        colume_erode = cv.erode(volume_img, np.ones((3, 3), np.uint8), iterations=1)
        volume = pytesseract.image_to_string(colume_erode, config='-l rus --psm 6')
        volume = re.findall(r'(\d{3}) Л', volume)
        if volume == list():
            volume = ''
        else:
            volume = volume[0]

        # Распознаем массу нетто на вырезанной части этикетки
        net_weight_x = logo_center[0] + 250
        net_weight_y = logo_center[1] + 650
        net_weight_img = gray[net_weight_y:net_weight_y + 150, net_weight_x:net_weight_x + 250]
        net_weight = pytesseract.image_to_string(net_weight_img, config='-l rus --psm 6')
        net_weight = re.findall(r'[^\d]{2}(\d\d\d)(\.\d{1,3})?[^\d]{2}', net_weight)
        if net_weight == list():
            net_weight = ''
        elif net_weight[0][1] == '':
            net_weight = net_weight[0][0]
        else:
            net_weight = net_weight[0][0] + net_weight[0][1]

        MQTT_MSG = json.dumps({"ID": 53005,
                               "Label": 'Yes',
                               "Timestamp": timestamp,
                               "Product name": name,
                               "Pocket order": pocket_order,
                               "EAN13": ean13,
                               "Volume": volume,
                               "Net weight": net_weight})
    else:
        timestamp = int(time.time())

        MQTT_MSG = json.dumps({"ID": 53005,
                               "Label": 'No',
                               "Timestamp": timestamp})

    msgs = [{'topic': "Line53BarrelLabel", 'payload': MQTT_MSG}]
    publish.multiple(msgs, hostname="192.168.1.6")

    # Соединяемся с сервером
    conn = SMBConnection(username='PopovVM', password='Pop123456', my_name='', remote_name='')
    assert (conn.connect('192.168.1.250'))

    # Создаем названия директории и файлов
    now = datetime.now()
    dir_name = now.strftime('53_1_%Y_%m_%d')
    file_name = now.strftime('%Y_%m_%d_%H_%M_%S.jpg')
    name_folder = conn.listPath('Lukoil', 'Label_Barrel/')

    # Перебираем директории
    listdir = []
    for name in name_folder:
        if name.filename is not listdir:
            listdir.append(name.filename)

    # Создаем новую директорию, если ее нет
    if dir_name not in listdir:
        conn.createDirectory('Lukoil', 'Label_Barrel/' + dir_name)

    # Открываем временный файл с обработанным изображением и записываем его, как файловый объект
    with open('temp_image.jpg', 'rb', buffering=0) as file_obj:
        conn.storeFile('Lukoil', 'Label_Barrel/' + dir_name + '/' + file_name, file_obj)
    file_obj.close()
