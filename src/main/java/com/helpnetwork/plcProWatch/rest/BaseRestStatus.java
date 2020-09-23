package com.helpnetwork.plcProWatch.rest;


/*import com.helpnetwork.MESicPLC.Pool.TaskExample;
import com.helpnetwork.MESicPLC.REST.model.BaseResponse;
import com.helpnetwork.MESicPLC.REST.model.PlcRequest;
import org.springframework.web.bind.annotation.*;*/

import com.helpnetwork.plcProWatch.pool.TaskExample;
import com.helpnetwork.plcProWatch.rest.model.BaseResponse;
import com.helpnetwork.plcProWatch.rest.model.PlcRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("scud_prowatch")
public class BaseRestStatus {


    private final String sharedKey = "SHARED_KEY";

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";
    private static final int CODE_SUCCESS = 100;
    private static final int AUTH_FAILURE = 102;

    static public boolean runThreadTask;

    public static boolean isRunThreadTask() {
        return runThreadTask;
    }

    public void setRunThreadTask(boolean runThreadTask) {
        this.runThreadTask = runThreadTask;
    }

    @GetMapping
    public BaseResponse showStatus() {
        return new BaseResponse(SUCCESS_STATUS, 1);
    }

    @PostMapping("/prowatchstart")
    public BaseResponse startPLCPool (@RequestParam(value = "key") String key, @RequestBody PlcRequest request) {

        final BaseResponse response;

        if (sharedKey.equalsIgnoreCase(key)) {
            int userId = request.getUserId();
            String itemId = request.getItemId();
            double discount = request.getDiscount();
            // Process the request
            // ....
            // Return success response to the client.
            System.out.println("Try Start Task pool DB ProWatch");
            StartTASKExample();
            response = new BaseResponse(SUCCESS_STATUS, CODE_SUCCESS);
        } else {
            response = new BaseResponse(ERROR_STATUS, AUTH_FAILURE);
        }
        return response;
    }

    @PostMapping("/prowatchstop")
    public BaseResponse stopPLC (@RequestParam(value = "key") String key, @RequestBody PlcRequest request){
        final BaseResponse response;

        if (sharedKey.equalsIgnoreCase(key)) {
            int userId = request.getUserId();
            String itemId = request.getItemId();
            double discount = request.getDiscount();
            // Process the request
            // ....
            // Return success response to the client.
            System.out.println("Request Stop DB ProWatch");
            StopTASKExample();
            response = new BaseResponse(SUCCESS_STATUS, CODE_SUCCESS);

        } else {
            response = new BaseResponse(ERROR_STATUS, AUTH_FAILURE);
        }

        return response;
    }

    public  static void StartTASKExample() {
        BaseRestStatus.runThreadTask =true;
        //this.runThreadTask = true;
       TaskExample mDBPoolRequest = new TaskExample();
        mDBPoolRequest.start();

    }
    public  void StopTASKExample() {
       // TaskExample ttt = new TaskExample();
       // ttt.setRunThreadTask(false);
        this.runThreadTask = false;
    }

    @PostMapping("/startlabelbarrel53")
    public BaseResponse startPythonCmd (@RequestParam(value = "key") String key, @RequestBody PlcRequest request) {
        final BaseResponse response;

        if (sharedKey.equalsIgnoreCase(key)) {
            int userId = request.getUserId();
            String itemId = request.getItemId();
            double discount = request.getDiscount();
            // Process the request
            // ....
            // Return success response to the client.
            System.out.println("Start Python OpenCV Label barrel");
            //String cmd = "python D:\\label_stas.py";
            String cmd = "python D:\\label.py";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response = new BaseResponse(SUCCESS_STATUS, CODE_SUCCESS);
        } else {
            response = new BaseResponse(ERROR_STATUS, AUTH_FAILURE);
        }
        return response;
    }


    }
