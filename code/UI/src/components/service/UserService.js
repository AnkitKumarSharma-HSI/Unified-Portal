import axios from "axios";

class UserService {
  static BASE_URL = "http://localhost:8080";
  static AWS_BASE_URL= "http://13.126.48.191:8080";

  static async login(email, password) {
    console.log("Login is called using the url "+UserService.AWS_BASE_URL);
    try {
      const response = await axios.post(`${UserService.AWS_BASE_URL}/auth/login`, {
        email,
        password,
      });
      return response.data;
    } catch (err) {
      console.log("error while login "+err);
      throw err;
    }
  }

  static async register(userData, token) {
    try {
      const response = await axios.post(
        `${UserService.BASE_URL}/auth/register`,
        userData,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }

  static async getAllUsers(token) {
    try {
      const response = await axios.get(
        `${UserService.BASE_URL}/admin/get-all-users`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Cache-Control": "no-cache",
          },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }

  static async getYourProfile(token) {
    try {
      const response = await axios.get(
        `${UserService.BASE_URL}/adminuser/get-profile`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }
  static async fileUpload(formData, token) {
    try {
      // Send the file to the backend (Spring Boot API)
      const response = await axios.post(
        `${UserService.BASE_URL}/api/upload/json`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // Handle response (success)
      alert("File Uploaded Successfully.");
      console.log(response.data);
      return "200";
    } catch (error) {
      // Handle error
      console.error("Error uploading the file", error);
      alert("File upload failed");
    }
  }
  static async addSchedule(formData, token) {
    try {
      const response = await axios.post(
        `${UserService.BASE_URL}/api/schedule/add`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert("Schedule added Successfully.");
      console.log(response.data);
      return response.data;
    } catch (error) {
      console.log("Error while adding schedule", error);
      alert("Add Schedule Failed");
    }
  }
  static async stopResumeScheduleForScenario(formData,token){
    try{
      const response=await axios.post( `${UserService.BASE_URL}/api/schedule/stop-resume`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        });
        return response;

    }catch(error){
      console.log("Error while stopping/resuming schedule",error);
      alert("Stop Scenario Failed!!");
    }
  }
  static async executeJson(userId, token) {
    console.log(userId + " " + token);
    try {
      const response = await axios.get(
        `${UserService.BASE_URL}/api/executejson/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response;
    } catch (error) {
      console.log("Error while fetching json data", error);
      alert("Json Execution Failed");
    }
  }
  static async getJsonData(userId, token) {
    try {
      const response = await axios.get(
        `${UserService.BASE_URL}/api/json/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response;
    } catch (error) {
      console.log("Error while fetching json data", error);
      alert("Json fetch failed");
    }
  }

  static async getUserById(userId, token) {
    try {
      const response = await axios.get(
        `${UserService.BASE_URL}/admin/get-users/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }

  static async deleteUser(userId, token) {
    try {
      const response = await axios.delete(
        `${UserService.BASE_URL}/admin/delete/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }

  static async updateUser(userId, userData, token) {
    try {
      const response = await axios.put(
        `${UserService.BASE_URL}/admin/update/${userId}`,
        userData,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (err) {
      throw err;
    }
  }

  /**AUTHENTICATION CHECKER */
  static logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
  }

  static isAuthenticated() {
    const token = localStorage.getItem("token");
    return !!token;
  }

  static isAdmin() {
    const role = localStorage.getItem("role");
    return role === "ADMIN";
  }

  static isUser() {
    const role = localStorage.getItem("role");
    return role === "USER";
  }

  static adminOnly() {
    return this.isAuthenticated() && this.isAdmin();
  }
  static userOnly() {
    return this.isAuthenticated();
  }
}

export default UserService;
