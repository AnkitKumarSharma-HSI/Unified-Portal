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
import { VscRunAllCoverage } from "react-icons/vsc";


function ProfilePage() {
  const [profileInfo, setProfileInfo] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [id, setId] = useState(0);
  const [jsonData, setJsonData] = useState(0);
  const [viewJson, setViewJSON] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [isExecutionStarted,setIsExecutionStarted]=useState(true);

  useEffect(() => {
    fetchProfileInfo();
  }, []);
  async function fetchJson(userId) {
    const token = localStorage.getItem("token");
    const res = await UserService.getJsonData(userId, token);
    console.log(res.data);
    setJsonData(res.data);
    console.log(jsonData);
  }

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
      setId(response.ourUsers.id);
      fetchJson(response.ourUsers.id);
      setProfileInfo(response.ourUsers);
    } catch (error) {
      console.error("Error fetching profile information:", error);
    }
  };
  const handleDownloadJson = () => {
    const blob = new Blob([JSON.stringify(jsonData)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `json_data_${id}.json`; // Specify the download filename
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };
  const handleExecuteJson=async()=>{
    if(isExecutionStarted===false){
      console.log("Hi Ankit");
      setIsExecutionStarted(true);
      return;
    }
    setIsExecutionStarted(false);
    const token = localStorage.getItem("token");
   console.log("Execute Json is called");
   const res = await UserService.executeJson(id,token);
   console.log(res);
  }
  const handleViewJSON = () => {
    setViewJSON(true);
    console.log("hello");
  };
  const closeJSONView = () => {
    setViewJSON(false);
    setIsCopied(false);
  };
  const copyToClipBoard = () => {
    if (isCopied !== true) {
      navigator.clipboard
        .writeText(JSON.stringify(jsonData))
        .then(() => {
          setIsCopied(true);
        })
        .catch((err) => {
          console.log("Unable to copy the Json");
          setIsCopied(false);
        });
    } else {
      alert("Data is copied successfully.");
    }
  };

  return (
    <div className="profile-page-container">
      {viewJson ? (
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
      ) : null}
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
      <div className="profile-page-btn-container">
        {profileInfo.role !== "ADMIN" ? (
          <form onSubmit={handleSubmit}>
          <fieldset>
          <legend>Upload Json</legend>
          <input type="file" accept=".json" onChange={handleFileChange} />
            <button className="uploadBtn" type="submit">
              <MdOutlineFileUpload />
            </button>
          </fieldset>
           
          </form>
        ) : null}
        {jsonData != 0 && profileInfo.role !== "ADMIN" ? (
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
        ) : null}

        {/* <button className='scheduleBtn'>Schedule</button>
                <button className='executeBtn'>Execute</button> */}
      </div>

      {jsonData != 0 && profileInfo.role !== "ADMIN" ? (
        <>
        <div className="excuteBtnContainer">
          <button className="executeJsonBtn" onClick={handleExecuteJson}> {isExecutionStarted?<><span className="executingIcon"><VscRunAll /></span>
          Execute</>:<><span className="executingIcon"><VscRunAllCoverage /></span>
          Executing</>}</button>

        </div>
        </>
      ):null}


    </div>
  );
}

export default ProfilePage;
