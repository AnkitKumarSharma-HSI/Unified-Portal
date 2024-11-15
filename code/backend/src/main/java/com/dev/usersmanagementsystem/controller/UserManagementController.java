package com.dev.usersmanagementsystem.controller;

import com.dev.usersmanagementsystem.dto.ReqRes;
import com.dev.usersmanagementsystem.entity.OurUsers;
import com.dev.usersmanagementsystem.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg){
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqres){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

//    @GetMapping("/api/json/{userId}")
//    public ResponseEntity<String> getJsonById(@PathVariable Integer userId) {
//        return usersManagementService.getJSONById(userId);
//    }
//    @GetMapping("/api/executejson/{userId}")
//    public ResponseEntity<String> executeJsonById(@PathVariable Integer userId) {
//        return usersManagementService.executeJsonById(userId);
//    }

    @PostMapping("/api/upload/json")
    public ResponseEntity<String> uploadJsonFile(@RequestParam("file") MultipartFile file, @RequestParam("id") String id,@RequestParam("code") String code,@RequestParam("desc") String desc) {
        System.out.println(id+" "+code+" "+desc );

        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty. Kindly select a different file and try again. Thanks", HttpStatus.BAD_REQUEST);
        }

        try {
            // Get the content of the uploaded file (raw JSON data)
            String jsonContent = new String(file.getBytes());

            // Process the file as needed (e.g., save it to a directory, process the content, etc.)
            ReqRes response=usersManagementService.fileUpload(jsonContent,id,code,desc);
            // Optionally, save the file to the server or process it
            // Files.write(Paths.get("path/to/save/file.json"), file.getBytes());

            return new ResponseEntity<>("File uploaded successfully!", HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error occurred while saving file"+e);
            return new ResponseEntity<>("Error processing the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/api/schedule/add")
    public ResponseEntity<ReqRes> addSchedule(@RequestParam("scenarioId")String scenarioId,@RequestParam("frequency") String frequency,@RequestParam("sdt") String sdt,@RequestParam("edt") String edt,@RequestParam("userId") String userId){
        try{
            ReqRes response=usersManagementService.saveScheduleInfo(userId,scenarioId,frequency,sdt,edt);
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error occurred while adding  schedule"+e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/api/schedule/stop-resume")
    public ResponseEntity<ReqRes> stopResumeSchedule(@RequestParam("scenarioId") String scenarioId,@RequestParam("userId") String userId,@RequestParam("state") String state){
        try{
            ReqRes response=usersManagementService.stopResumeSchedule(userId, scenarioId,state);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while stopping schedule"+e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
