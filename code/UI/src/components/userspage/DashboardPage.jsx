import React, { useState } from "react";
import UserService from "../service/UserService";
import { useEffect } from "react";

function DashboardPage() {
  const [company, setCompany] = useState("");
  useEffect(() => {
    fetchProfileInfo();
  }, []);
  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token"); // Retrieve the token from localStorage
      const response = await UserService.getYourProfile(token);
      console.log("Hi " + response.ourUsers.name);
      setCompany(response.ourUsers.name);
      console.log(response.ourUsers.scenarios);
      //   setId(response.ourUsers.id);
      //   setScenarios(response.ourUsers.scenarios);
      //   // fetchJson(response.ourUsers.id);
      //   setProfileInfo(response.ourUsers);
    } catch (error) {
      console.error("Error fetching profile information:", error);
    }
  };
  return (
    <>
      <div className="dashboardPageContainer">
        {company == "UBI" ? (
          <div className="frameContent" style={{"width":"100%",height:"100vh"}}>
            <iframe src="http://13.126.48.191:7000/d/ad7cee07-4fa2-456f-bc37-c804abe9e64c/ubi?orgId=1&refresh=5s&from=1728043405000&to=1728048330000&theme=light" frameborder="0" width="100%" height="100%" style={{marginBottom:"69px"}}></iframe>
          </div>
        ) : <p>No Data Available</p>}
        
        <div className="overlayDiv1"></div>
        <div className="overlayDiv2"></div>
  

      </div>
    </>
  );
}
export default DashboardPage;
