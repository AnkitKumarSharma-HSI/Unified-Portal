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
        {company == "PNB" ? (
          <div className="graphContainer">
            <iframe
              src="http://13.126.48.191:7000/d-solo/ad7cee07-4fa2-456f-bc37-c804abe9e64c/ubi?orgId=1&refresh=5s&from=1728043405000&to=1728048330000&theme=light&panelId=1"
              width="100%"
              height="100%"
              frameBorder="0"
            ></iframe>
            <div className="frameContainer">
              <div className="frame1">
                <iframe
                  src="http://13.126.48.191:7000/d-solo/ad7cee07-4fa2-456f-bc37-c804abe9e64c/ubi?orgId=1&refresh=5s&from=1728043405000&to=1728048330000&theme=light&panelId=3"
                  width="100%"
                  height="100%"
                  frameborder="0"
                ></iframe>
              </div>
              <div className="frame2">
                <iframe
                  src="http://13.126.48.191:7000/d-solo/ad7cee07-4fa2-456f-bc37-c804abe9e64c/ubi?orgId=1&refresh=5s&from=1728043405000&to=1728048330000&theme=light&panelId=6"
                  width="100%"
                  height="100%"
                  frameborder="0"
                ></iframe>
              </div>
            </div>
            <div className="frame3">
              <iframe
                src="http://13.126.48.191:7000/d-solo/ad7cee07-4fa2-456f-bc37-c804abe9e64c/ubi?orgId=1&refresh=5s&from=1728043405000&to=1728048330000&theme=light&panelId=2"
                width="100%"
                height="100%"
                frameborder="0"
              ></iframe>
            </div>
          </div>
        ) : null}
      </div>
    </>
  );
}
export default DashboardPage;
