import React from 'react';
import UserService from "../service/UserService";
import { useEffect } from 'react';

  
  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token"); // Retrieve the token from localStorage
      const response = await UserService.getYourProfile(token);
      console.log("Hi " + response.ourUsers.name);
      console.log(response.ourUsers.scenarios);
    //   setId(response.ourUsers.id);
    //   setScenarios(response.ourUsers.scenarios);
    //   // fetchJson(response.ourUsers.id);
    //   setProfileInfo(response.ourUsers);
    } catch (error) {
      console.error("Error fetching profile information:", error);
    }
  };

function DashboardPage(){
    useEffect(() => {
        fetchProfileInfo();
      }, []);
    return(<>
    <div className="dashboardPageContainer">
        <div className="graphContainer">
            <iframe src="http://localhost:4000/d-solo/ce3sgnm1w1tkwc/tmi?from=1731452851887&to=1731474451896&timezone=browser&orgId=1&theme=light&panelId=1&__feature.dashboardSceneSolo&kiosk=true&hide_controls=true" width="100%" height="100%" frameBorder="0"></iframe>

        </div>

    </div>
        
    </>);
}
export default DashboardPage;
