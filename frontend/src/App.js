import React, { useState } from "react";
import './App.css';
import Navbar from './components/Navbar.js';
import Content from './components/Content.js';
import Login from './components/Login.js';
import Footer from './components/Footer.js';
import QnA from './components/QnA.js';
import Signup from './components/Sign.js';



function App() {
 
    const [islogin, setIslogin] = useState(true);
    const togglelogin=()=>{setIslogin(!islogin)}

    return (
    <>
    
       
       
     
        <Navbar></Navbar>
        {/* <div className="container"> */}
        <Content></Content>
        {islogin?
        <Login></Login>:
        
        <Signup></Signup>}
        <div className="loginmessage">
        <a href="/#"onClick={togglelogin} >{islogin ? <h1 >Not yet registered,Sign up</h1>: <h1>Already have an account,login now</h1> } </a>
        </div>
        {/* </div> */}

        <div className="question">
          <QnA></QnA>
        </div>
        
        <div className="footer">
          <Footer></Footer>
        </div>
        
        
        </>
     
  );
}

export default App;
