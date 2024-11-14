import React, { useState, useEffect } from "react";
import UserService from "../service/UserService";
import { Link } from "react-router-dom";
import { FaUserTie } from "react-icons/fa";
import { RxUpdate } from "react-icons/rx";
import { MdOutlineMailOutline } from "react-icons/md";
import { MdOutlineLocationCity } from "react-icons/md";
import { FaFileUpload } from "react-icons/fa";
import { MdOutlineFileUpload } from "react-icons/md";
import { FaFileDownload } from "react-icons/fa";
import { VscOpenPreview } from "react-icons/vsc";
import { IoClose } from "react-icons/io5";
import { MdOutlineContentCopy } from "react-icons/md";
import { MdAdminPanelSettings } from "react-icons/md";
import { VscRunAll } from "react-icons/vsc";
import { FaCopy } from "react-icons/fa";
import { LuCopy } from "react-icons/lu";
import { LuCopyCheck } from "react-icons/lu";
import { IoMdEye } from "react-icons/io";
import { RiCalendarScheduleLine } from "react-icons/ri";
import { RiCalendarScheduleFill } from "react-icons/ri";
import { LuFileJson2 } from "react-icons/lu";
import { TbFileDescription } from "react-icons/tb";
import { BsStopCircle } from "react-icons/bs";
import { FiDownload } from "react-icons/fi";
import { BsFiletypeJson } from "react-icons/bs";
import { BsSave2 } from "react-icons/bs";
import { RxResume } from "react-icons/rx";
import { FaRegPauseCircle } from "react-icons/fa";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import { MdUploadFile } from "react-icons/md";
import { BsUpload } from "react-icons/bs";







import { VscRunAllCoverage } from "react-icons/vsc";

function ProfilePage() {
  const [profileInfo, setProfileInfo] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [code, setCode] = useState("");
  const [desc, setDesc] = useState("");
  const [id, setId] = useState(0);//user id
  const [scenarios, setScenarios] = useState([]);
  const [jsonData, setJsonData] = useState(0);
  const [viewJson, setViewJSON] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [isExecutionStarted, setIsExecutionStarted] = useState(true);
  const [isScenarioVisible, setScenarioVisible] = useState(false);
  const [isSContainerVisible, setIsSContainer] = useState(false);
  const[frequencey,setFrequency]=useState(0);
  const[startDateTime,setStartDateTime]=useState("");
  const[endDateTime,setEndDateTime]=useState("");
  const[scheduleError,setScheduleError]=useState("");
  const[stopVisible,setStopVisible]=useState(false);

  const[scenarioId,setScenarioId]=useState(0);//scenario id when user is clicking on the schedule button


  useEffect(() => {
    fetchProfileInfo();
  }, []);
  // async function fetchJson(userId) {
  //   const token = localStorage.getItem("token");
  //   const res = await UserService.getJsonData(userId, token);
  //   console.log(res.data);
  //   setJsonData(res.data);
  //   console.log(jsonData);
  // }

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!selectedFile) {
      alert("Please select a file first.");
      return;
    }
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("id", id);
    formData.append("code", code);
    formData.append("desc", desc);

    try {
      const token = localStorage.getItem("token");
      const response = await UserService.fileUpload(formData, token);
      window.location.reload();
    } catch (error) {
      console.error("Error uploading the file", error);
    }
  };

  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token"); // Retrieve the token from localStorage
      const response = await UserService.getYourProfile(token);
      console.log("Hi" + response.ourUsers.id);
      console.log(response.ourUsers.scenarios);
      setId(response.ourUsers.id);
      setScenarios(response.ourUsers.scenarios);
      // fetchJson(response.ourUsers.id);
      setProfileInfo(response.ourUsers);
    } catch (error) {
      console.error("Error fetching profile information:", error);
    }
  };
  const handleDownloadJson = (s_id) => {
    for (let i = 0; i < scenarios.length; i++) {
      if (scenarios[i].scenario_id === s_id) {
        setJsonData(scenarios[i].jsonFile);
      }
    }
    const blob = new Blob([JSON.stringify(jsonData)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `json_data_${s_id}.json`; // Specify the download filename
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };
  const handleExecuteJson = async () => {
    if (isExecutionStarted === false) {
      console.log("Hi Ankit");
      setIsExecutionStarted(true);
      return;
    }
    setIsExecutionStarted(false);
    const token = localStorage.getItem("token");
    console.log("Execute Json is called");
    const res = await UserService.executeJson(id, token);
    console.log(res);
  };
  const handleViewJSON = (s_id) => {
    console.log("Scenario Id " + s_id);
    // console.log(scenarios);
    for (let i = 0; i < scenarios.length; i++) {
      if (scenarios[i].scenario_id === s_id) {
        setJsonData(scenarios[i].jsonFile);
      }
    }
    setViewJSON(true);
  };
  const closeJSONView = () => {
    setScenarioVisible(false);
    // setIsCopied(false);
    // setViewJSON(false);
    // setIsCopied(false);
  };
  const closeJSONPreview = () => {
    setViewJSON(false);
    setIsCopied(false);
  };
  const handleVisibleJsonContainer = () => {
    setScenarioVisible(true);
    setIsCopied(false);
  };
  const copyToClipBoard = () => {
    navigator.clipboard
      .writeText(JSON.stringify(jsonData))
      .then(() => {
        setIsCopied(true);
      })
      .catch((err) => {
        console.log("Unable to copy the Json");
        setIsCopied(false);
      });
  };
  const closeSchedulerView = () => {
    setIsSContainer(false);
  };
  const openSchedulerView = (s_id) => {
    console.log(s_id);
    setScenarioId(s_id);
    setIsSContainer(true);
  };
  const handleScheduleSumbit=async (e)=>{
    e.preventDefault();
    // if(frequencey<=2){
    //   setScheduleError("Frequency should be positive and more than 2 minutes");
    //   return;

    // }

    console.log("Frequency"+" "+frequencey+" "+startDateTime+" "+endDateTime+" scenario_id "+scenarioId+" "+id);
    //addSchedule
    const formData = new FormData();
    formData.append("scenarioId", scenarioId);
    formData.append("frequency", frequencey);
    formData.append("sdt", startDateTime);
    formData.append("edt", endDateTime);
    formData.append("userId",id);

    try {
      const token = localStorage.getItem("token");
      const response = await UserService.addSchedule(formData, token);
      if(response.statusCode==200){
        window.location.reload();
      }
    } catch (error) {
      console.error("Error uploading the file", error);
    }

  };
  const convertDateFormat=(millisecondsTime)=>{
    const date = new Date(millisecondsTime);
    const utcString = date.toUTCString(); 
    const modifiedDateString = utcString.replace(" GMT", "");
    // Format the date and time
    // const localString = date.toLocaleString('en-US', {
    //   year: 'numeric',
    //   month: '2-digit',
    //   day: '2-digit',
    //   hour: '2-digit',
    //   minute: '2-digit',
    //   second: '2-digit',
    //   hour12: false // Change to false for 24-hour format
    // });

  return modifiedDateString;
  
  };
  const handleStopAction=async(sId,e,status)=>{
    e.preventDefault();
    console.log("status"+status);

    let state=status;
    if(state==="Active"){
      state="Inactive";
    }else if(state==="Inactive"){
      state="Active";
    }

    const formData = new FormData();
    formData.append("scenarioId", sId);
    formData.append("userId", id);
    formData.append("state",state);
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.stopResumeScheduleForScenario(formData, token);
      console.log(response);
      if(response.data.statusCode==200){
        window.location.reload();
      }
    } catch (error) {
      console.error("Error uploading the stopping/resuming execution", error);
    }

  }

  return (
    <div className="profile-page-container">
      {isSContainerVisible ? (
        <>
          <div className="scheduleContainer">
            <span
              className="closeSchedulerView"
              onClick={closeSchedulerView}
              style={{
                fontSize: "30px",
                cursor: "pointer",
              }}
            >
              <IoClose />
            </span>
            <div className="subSchedulerContainer">
              <h2 style={{color:"white"}}><RiCalendarScheduleLine />
               Schedule</h2>
              <form onSubmit={handleScheduleSumbit}>
                <input type="number" placeholder="Frequency In Minutes" className="json-field frequencey" name="frequency" id="frequency" onChange={(e)=>{setFrequency(e.target.value)}} /> 
                <br />
                <label htmlFor="startDateInput">Start Date and Time: </label>
                <input type="datetime-local" className="startDateInput" id="startDateInput" onChange={(e)=>{setStartDateTime(e.target.value)}}/>
                <br />
                <label htmlFor="endDateTimeInput">End Date and Time:</label>
                <input type="datetime-local" name="endDateTimeInput" id="endDateTimeInput"  onChange={(e)=>{setEndDateTime(e.target.value)}}/>
                <p style={{color:"red"}}>{scheduleError} </p>
                <button className="scheduleSubmitBtn" type="submit">
                  <BsSave2/>Save
                </button>


              </form>

            </div>
          </div>
        </>
      ) : null}
      {/* {viewJson ? (
        <div className="viewJsonContainer">
          <button className="copyJsonBtn" onClick={copyToClipBoard}>
            <MdOutlineContentCopy />
            {isCopied ? "Copied" : "Copy"}
          </button>
          <button onClick={closeJSONView} className="closeJSONView">
            <IoClose />
          </button>
          <div className="viewJSONChild"> ${JSON.stringify(jsonData)}</div>
        </div>
      ) : null} */}

      <h2 style={{ textTransform: "uppercase", fontWeight: "700" }}>
        <MdAdminPanelSettings />
        <span className="profileUserName">{profileInfo.name}</span>
      </h2>
      {/* <p>Name: {profileInfo.name}</p> */}
      <p>
        <MdOutlineMailOutline />
        Mail: {profileInfo.email}
      </p>
      <p>
        <MdOutlineLocationCity />
        City: {profileInfo.city}
      </p>
      {profileInfo.role === "ADMIN" && (
        <button>
          <Link to={`/update-user/${profileInfo.id}`}>
            <RxUpdate />
            Update
          </Link>
        </button>
      )}
      {profileInfo.role !== "ADMIN" && (
        <button onClick={handleVisibleJsonContainer} style={{
    padding: "5px 0px 5px 0px",
    fontSize: "20px"
}}>
         
          <span><MdOutlineFileUpload /></span>
        
          Scenario
        </button>
      )}
      {viewJson ? (
        <div className="viewJsonContainer">
          <span className="copyJSONView" onClick={copyToClipBoard}>
            {isCopied ? <LuCopyCheck /> : <LuCopy />}
          </span>
          <span
            className="closeJSONView"
            onClick={closeJSONPreview}
            style={{
              fontSize: "30px",
              cursor: "pointer",
            }}
          >
            <IoClose />
          </span>
          <div className="viewJSONChild"> {JSON.stringify(jsonData)}</div>
        </div>
      ) : null}
     <div className="calenderContainer">
      {/* <Calendar/> */}

      </div> 
      <div className="profile-page-btn-container">
        {isScenarioVisible ? (
          <div className="upload-json-container">
            {/* <button className="closeJSONView" onClick={closeJSONView} style={{width:'50px', borderRadius:'10px',fontSize:'20px'}}>
              <IoClose />
            </button> */}
            <span
              className="closeJSONView"
              onClick={closeJSONView}
              style={{
                fontSize: "30px",
                cursor: "pointer",
              }}
            >
              <IoClose />
            </span>
            <div className="upload-json-subcontainer">
              <form onSubmit={handleSubmit}>
                <h2 style={{color:"white"}}><LuFileJson2 />
                Scenario</h2>
                <input
                  type="text"
                  placeholder="Code"
                  name="code"
                  className="json-field json-code"
                  onChange={(e) => {
                    setCode(e.target.value);
                  }}
                />
                <br />
                <input
                  type="text"
                  placeholder="Description"
                  name="description"
                  className="json-field json-description"
                  onChange={(e) => {
                    setDesc(e.target.value);
                  }}
                />
                <br />
                {/* <label for="file-upload" class="custom-file-upload">
                  Browse JSON
                </label> */}
                <input
                  type="file"
                  name="file-upload"
                  id="file-upload"
                  accept=".json"
                  onChange={handleFileChange}
                />
                <button className="jsonuploadBtn" type="submit">
                <BsSave2/>Save
                </button>
              </form>
            </div>
          </div>
        ) : null}

        {profileInfo.role !== "ADMIN" && scenarios.length > 0 ? (
          <>
            <br />

            <table className="userScenarioTable">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Description</th>
                  <th>Schedule Information</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {scenarios.map((scenario) => (
                  <tr key={scenario.scenario_id}>
                    <td>{scenario.code}</td>
                    <td style={{maxWidth:"100px"}}>{scenario.description}</td>
                    <td><div>
                    <p>Frequency: {scenario.schedule?scenario.schedule['frequency']+" "+'minutes':"NA"} </p>
                    <p>Start Date and Time: {scenario.schedule?convertDateFormat(scenario.schedule['startTimeInMillis']):"NA"} </p>
                    <p>End Date and Time: {scenario.schedule? convertDateFormat(scenario.schedule['endTimeInMillis']):"NA"} </p>

                    </div></td>
                    <td className="actionContainerSchedule">
                      <button
                        onClick={() => {
                          handleViewJSON(scenario.scenario_id);
                        }}
                      >
                        <IoMdEye />
                        Preview
                      </button>
                      {/* <span onClick={()=>{
                handleViewJSON(scenario.scenario_id)
              }}></span> */}
                      <button
                        onClick={() => handleDownloadJson(scenario.scenario_id)}
                      >
                      
                        <FiDownload />
                        Download
                      </button>

                      <button
                        onClick={() => openSchedulerView(scenario.scenario_id)}
                      >
                       <RiCalendarScheduleFill />
                       Schedule
                      </button>
                      {scenario.schedule?<>
                        <button onClick={(e)=>{handleStopAction(scenario.scenario_id,e,scenario.status)}}>
                      {scenario.status=="Active"?
                      <>
                      <span style={{color:"white"}}>
                      <FaRegPauseCircle />Stop</span>
                      </>
                      :<>
                        <span style={{color:"white"}}>
                        {/* #1ff91f */}
                        <RxResume />
                        Resume</span>
                      </>}
                      
                     </button>
                      </>:null}
                    
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        ) : profileInfo.role !== "ADMIN" ? (
          <p>No Scenarios are avaialable to display.</p>
        ) : null}

        {/* {profileInfo.role !== "ADMIN" ? (
          <form onSubmit={handleSubmit}>
          <fieldset>
          <legend>Upload Json</legend>
          <input type="file" accept=".json" onChange={handleFileChange} />
            <button className="uploadBtn" type="submit">
              <MdOutlineFileUpload />
            </button>
          </fieldset>
           
          </form>
        ) : null} */}
        {/* {jsonData != 0 && profileInfo.role !== "ADMIN" ? (
          <>
            <form className="jsonActionBtnContainer" onSubmit={(e)=>{e.preventDefault()}}>
              <fieldset>
                <legend>Existing JSON</legend>
                {jsonData != 0 && profileInfo.role !== "ADMIN" ? (
                  <>
                    <button
                      onClick={handleDownloadJson}
                      className="downloadJSONBtn"
                    >
                      <FaFileDownload />
                      Download
                    </button>
                    <button onClick={handleViewJSON} className="previewJsonBtn">
                      <VscOpenPreview />
                      View
                    </button>
                  </>
                ) : null}
              </fieldset>
            </form>
          </>
        ) : null} */}

        {/* <button className='scheduleBtn'>Schedule</button>
                <button className='executeBtn'>Execute</button> */}
      </div>

      {/* {jsonData != 0 && profileInfo.role !== "ADMIN" ? (
        <>
        <div className="excuteBtnContainer">
          <button className="executeJsonBtn" onClick={handleExecuteJson}> {isExecutionStarted?<><span className="executingIcon"><VscRunAll /></span>
          Execute</>:<><span className="executingIcon"><VscRunAllCoverage /></span>
          Executing</>}</button>

        </div>
        </>
      ):null} */}
    </div>
  );
}

export default ProfilePage;
