import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import UserService from "../service/UserService";
import Navbar from "../common/Navbar";
import { PiUserFocusFill } from "react-icons/pi";
// import { LiaUserCircle } from "react-icons/lia";
import { HiMiniUserCircle } from "react-icons/hi2";
import { FaHouseUser } from "react-icons/fa";





function LoginPage(){
const [email, setEmail] = useState('')
const [password, setPassword] = useState('')
const [error, setError] = useState('')
const navigate = useNavigate();

const handleSubmit = async (e) => {
    e.preventDefault();

    try {
        const userData = await UserService.login(email, password)
        console.log(userData)
        if (userData.token) {
            localStorage.setItem('token', userData.token)
            localStorage.setItem('role', userData.role)     
            navigate('/profile');
            window.location.reload();
            

        }else{
            setError(userData.message)
        }
        
    } catch (error) {
        console.log(error)
        setError(error.message)
        setTimeout(()=>{
            setError('');
        }, 5000);
    }
}


    return(
        <div className="auth-container auth-container-login">
            <h2><HiMiniUserCircle />


</h2>
            {error && <p className="error-message">{error}</p>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Email: </label>
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                </div>
                <div className="form-group">
                    <label>Password: </label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    )

}

export default LoginPage;