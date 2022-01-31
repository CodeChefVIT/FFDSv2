import React, { useState } from "react";
import "./App.css";
import Navbar from "./components/Navbar.js";
import Footer from "./components/Footer.js";
import QnA from "./components/QnA.js";
import Moreinfo from "./Pages/Moreinfo.js";
import Account from "./Pages/Account.js";
import Match from "./Pages/Match.js";
import LandingSection from "./components/LandingSection/LandingSection";
import Matches from "./Pages/Matches";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

function App() {
  const [islogin, setIslogin] = useState(true);
  // const [loggedin] = useState(true);
  // const togglelogin=()=>{setIslogin(!islogin)}
  // const togglelogin=()=>{setmoreinfo(!loggedin)}
  const [loggedin, setLoggedin] = useState(true);
  return (
    <>
    <Router>
      <Navbar toggle={(gaga) => setIslogin(gaga)}></Navbar>
      <Switch>
          <Route exact path="/Moreinfo">
            <Moreinfo/>
          </Route>
          <Route exact path="/Matches">
            <Matches/>
          </Route>
          <Route exact path="/">
            {loggedin ? (
              <LandingSection/>
            ) : (
              <Account switch={islogin} toggle={(gaga) => setIslogin(gaga)}/>
            )}

          <div className="question">
            <QnA/>
          </div>

          <div className="footer">
            <Footer/>
          </div>
        </Route>
      </Switch>    
    </Router>
    </>
  );
}

export default App;
