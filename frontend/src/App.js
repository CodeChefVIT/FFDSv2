import React, { useState } from "react";
import "./App.css";
import Navbar from "./components/Navbar.js";
import Footer from "./components/Footer.js";
import QnA from "./components/QnA.js";
import Moreinfo from "./Pages/Moreinfo.js";
import Account from "./Pages/Account.js";
import Match from "./Pages/Match.js";
import LandingSection from "./components/LandingSection/LandingSection";
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

function App() {
  const [islogin, setIslogin] = useState(true);
  // const [loggedin] = useState(true);
  // const togglelogin=()=>{setIslogin(!islogin)}
  // const togglelogin=()=>{setmoreinfo(!loggedin)}
  const [loggedin, setLoggedin] = useState(true);
  return (
    <>
      <Navbar toggle={(gaga) => setIslogin(gaga)}></Navbar>
      {/* <LandingSection/> */}
      {loggedin ? (
        <LandingSection/>
      ) : (
        <Account switch={islogin} toggle={(gaga) => setIslogin(gaga)}></Account>
      )}

      <div className="question">
        <QnA></QnA>
        {/* <Match></Match> */}
      </div>

      <div className="footer">
        <Footer></Footer>
      </div>
    </>
  );
}

export default App;
