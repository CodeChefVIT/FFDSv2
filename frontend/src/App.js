import React, { useState } from "react";
import './App.css';
import Navbar from './components/Navbar.js';

import Footer from './components/Footer.js';
import QnA from './components/QnA.js';

import Account from './Pages/Account.js';




function App() {
 
    const [islogin, setIslogin] = useState(true);
    // const [loggedin] = useState(true);
    // const togglelogin=()=>{setIslogin(!islogin)}
    // const togglelogin=()=>{setmoreinfo(!loggedin)}

    return (
    <>
    
       
       
     
        <Navbar toggle={gaga=>setIslogin(gaga)}></Navbar>
      
        <Account switch={islogin} toggle={gaga=>setIslogin(gaga)}></Account>

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
