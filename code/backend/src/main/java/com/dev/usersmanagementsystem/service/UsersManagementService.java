package com.dev.usersmanagementsystem.service;

import com.dev.usersmanagementsystem.App;
import com.dev.usersmanagementsystem.dto.ReqRes;
import com.dev.usersmanagementsystem.entity.ExecutionTime;
import com.dev.usersmanagementsystem.entity.OurUsers;
import com.dev.usersmanagementsystem.entity.Scenario;
import com.dev.usersmanagementsystem.entity.Schedule;
import com.dev.usersmanagementsystem.repository.ExecutionTimeRepo;
import com.dev.usersmanagementsystem.repository.ScenarioRepo;
import com.dev.usersmanagementsystem.repository.ScheduleRepo;
import com.dev.usersmanagementsystem.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private ScenarioRepo scenarioRepo;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private ExecutionTimeRepo executionTimeRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");


    public ReqRes register(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();

        try {
            OurUsers ourUser = new OurUsers();
//            String userDb=registrationRequest.getName().toUpperCase();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//            ourUser.setUserDbName(userDb);
            OurUsers ourUsersResult = usersRepo.save(ourUser);
//            String createDatabaseQuery = "CREATE DATABASE " + userDb;
//            jdbcTemplate.execute(createDatabaseQuery);
            if (ourUsersResult.getId()>0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
//            if(user!=null){
//                UserContext.setCurrentUserDatabase(user.getUserDbName());
//
//                // Add new data source for the user's database
//                dynamicDataSource.addDataSource(user.getUserDbName(),dataSource);
//            }
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }





    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsers> result = usersRepo.findAllUsers();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }


    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }
//    public ResponseEntity<String> getJSONById(int id){
//        return usersRepo.findById(id)
//                .map(entity -> ResponseEntity.ok(entity.getJsonFile()))
//                .orElse(ResponseEntity.notFound().build());
//    }
//    public ResponseEntity<String> executeJsonById(int id){
//        return usersRepo.findById(id)
//                .map(entity -> {
//                    // Assuming getJsonFile() retrieves the JSON content as a String
//                    String jsonContent = entity.getJsonFile();
//                    App obj = new App();
//                    try {
//                        obj.setup();
//                        obj.runCode(jsonContent);
//                    } catch (Exception e) {
//                        System.out.println(e);
//                    }
//                    return ResponseEntity.ok(jsonContent);
//                })
//                .orElse(ResponseEntity.notFound().build());
//
//    }
    public ReqRes fileUpload(String json,String id,String code,String desc) throws Exception{
        ReqRes reqRes=new ReqRes();
        try{
            Optional<OurUsers> userOptional = usersRepo.findById(Integer.parseInt(id));
            if(userOptional.isPresent()){
                OurUsers existingUser = userOptional.get();
                List<Scenario> scenarioList=existingUser.getScenarios();
                Scenario scenario = new Scenario();
                scenario.setUser_id(existingUser.getId());
                scenario.setJsonFile(json);
                scenario.setCode(code);
                scenario.setDescription(desc);
                scenario.setStatus("Active");
                scenarioList.add(scenario);

//                if(existingUser.getJsonFile()!=null && !existingUser.getJsonFile().isEmpty()){
//                    existingUser.setPreviousJsonFile(existingUser.getJsonFile());
//                    existingUser.setJsonFile(json);
//                }else{
//                    existingUser.setJsonFile(json);
//                }
                scenarioRepo.saveAll(scenarioList);
//                existingUser.setScenarios(scenarioList);
//                OurUsers savedUser = usersRepo.save(existingUser);
//                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred saving user json file: " + e.getMessage());
            throw new Exception("Error occurred while saving json file");

        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
    public ReqRes saveScheduleInfo(String userId,String scenarioId,String frequency,String startDateTime,String endDateTime){
        ReqRes reqRes = new ReqRes();
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDateTime, formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDateTime, formatter);

        long startTimeInMilli = startLocalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTimeInMilli = endLocalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        try{
            Optional<Scenario> scenarioOptional = scenarioRepo.findScenarioByScenario_idAndUser_id(Integer.parseInt(scenarioId),Integer.parseInt(userId));
            if(scenarioOptional.isPresent()){
                Scenario scenario = scenarioOptional.get();
                Schedule schedule = new Schedule();
                schedule.setSchedule_id(Integer.parseInt(scenarioId));
                schedule.setFrequency(Integer.parseInt(frequency));
                schedule.setStartTimeInMillis(startTimeInMilli);
                schedule.setEndTimeInMillis(endTimeInMilli);
                schedule.setUserId(scenario.getUser_id());
                scheduleRepo.save(schedule);
                List<ExecutionTime> executionTimes = new ArrayList<>();
                long frequencyInMillis = (long) Integer.parseInt(frequency) * 60 * 1000;
                while (startTimeInMilli <= endTimeInMilli) {
                    ExecutionTime executionTime = new ExecutionTime();
                    executionTime.setStartTimeInMillis(startTimeInMilli);
                    executionTime.setScenarioId(Integer.parseInt(scenarioId));
                    executionTime.setUserId(Integer.parseInt(userId));
                    executionTimes.add(executionTime);
                    startTimeInMilli += frequencyInMillis;
                }
                executionTimeRepo.saveAll(executionTimes);
                scenario.setSchedule(schedule);
                scenarioRepo.save(scenario);
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");

            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occured while saving schedule: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return reqRes;
    }
    @Scheduled(fixedRate = 60000)
    public void scheduleExecution() {
        Long currentTimeInMillis = Instant.now().toEpochMilli()+19800000;
        Instant instant = Instant.ofEpochMilli(currentTimeInMillis);

        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));
        utcDateTime=utcDateTime.withSecond(0).withNano(0);

        long utcMilliseconds = utcDateTime.toInstant().toEpochMilli();

        System.out.println("Hello==="+utcMilliseconds);
        List<ExecutionTime> executionTimes = executionTimeRepo.findByStartTimeInMillis(utcMilliseconds);
        System.out.println("Execution time"+ executionTimes);
        for (ExecutionTime executionTime : executionTimes) {
            Optional<Scenario> scenario = scenarioRepo.findScenarioByScenario_idAndUser_id(executionTime.getScenarioId(), executionTime.getUserId());
            System.out.println("New scenario"+scenario);
            if(scenario.isPresent()){
                String jsonContent=scenario.get().getJsonFile();
                App obj = new App();
                try {
                        obj.setup();
                        obj.runCode(jsonContent);
                } catch (Exception e) {
                        System.out.println("Exception occurred while executing json"+e);
                }
                System.out.println("Time match"+scenario.get().getJsonFile());
            }

        }
    }
}
