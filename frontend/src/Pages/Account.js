import { useState } from "react";
import Login from "../components/Login";
import Signup from "../components/Sign.js";
import Moreinfo from "../components/Moreinfo.js";

function Account(props) {
  const [loggedin, setLoggedin] = useState(false);

  return (
    <>
      {loggedin ? (
        <Moreinfo></Moreinfo>
      ) : props.switch ? (
        <Login></Login>
      ) : (
        <Signup></Signup>
      )}

      <div className="loginmessage">
        <div
          className="col-lg-6 col-sm-12"
          style={{ padding: "50px 0px  50px 100px" }}
        >
          <a href="/#" onClick={() => props.toggle(!props.switch)}>
            {props.switch ? (
              <h4>Not yet registered, Sign up</h4>
            ) : (
              <h4>Already have an account, login now</h4>
            )}
          </a>
        </div>
      </div>
    </>
);
}

export default Account;
