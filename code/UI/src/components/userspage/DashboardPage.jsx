import React, { useState } from "react";
import UserService from "../service/UserService";
import { useEffect } from "react";

function DashboardPage() {
  const [company, setCompany] = useState("");
  const [dashboardUrl,setDashboardUrl]=useState("");
  useEffect(() => {
    fetchProfileInfo();
  }, []);
  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token"); // Retrieve the token from localStorage
      const response = await UserService.getYourProfile(token);
      // console.log("Hi " + response.ourUsers.name);
      setCompany(response.ourUsers.name);
      // console.log(response.ourUsers.dashboardUrl);
      setDashboardUrl(response.ourUsers.dashboardUrl);
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
    
        <div className="frameContent" style={{"width":"100%",height:"100vh"}}>
            <iframe src={dashboardUrl} frameborder="0" width="100%" height="100%" style={{marginBottom:"69px"}}></iframe>
          </div>
        <div className="overlayDiv1"></div>
        <div className="overlayDiv2"></div>
  

      </div>
    </>
  );
}
export default DashboardPage;
