import React, { useState } from "react";
import '../components/Login.css';
import './Moreinfo.css';
// import Footer from "../components/Footer";
// import '../components/ProfileSection/ProfileSection.css';

function Moreinfo() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function validateForm() {
    return email.length > 0 && password.length > 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
  }

  return (
    <>
      <div className="MoreInfoTitle my-4">
        <h2 className="text-center">Tell Us About Yourself !</h2>
      </div>
      {/* <div className="container MoreInfoForm">
        <form>
          <div className="form-floating mb-3">
            <input type="text" className="form-control" id="floatingInput" placeholder="Khush Sharma" />
            <label for="floatingInput">Full Name</label>
          </div>
          <div className="mb-3">
            <label for="exampleFormControlTextarea1" className="form-label">Hobbies/Something About Yourself: </label>
            <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Share a few lines about yourself"></textarea>
          </div>
          <div className="mb-3">
            <label for="exampleFormControlTextarea1" className="form-label">What Qualities Do You Seek in Your Match?</label>
            <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" Placeholder="Mention Points"></textarea>
          </div>

          <div className="mb-3">
            <label for="autoSizingSelect">Select Your Gender</label>
            <select className="form-select" id="autoSizingSelect">
              <option selected>Choose Your Gender...</option>
              <option value="1">Male</option>
              <option value="2">Female</option>
              <option value="3">Other</option>
            </select>
          </div>

          <div className="form-floating my-4">
            <input type="numbers" className="form-control" id="floatingPassword" placeholder="Phone" />
            <label for="floatingPassword">Contact Number</label>
          </div>

          <div className="mb-4">
            <label for="formFile" className="form-label">TimeTable Upload:</label>
            <input className="form-control" type="file" id="formFile"/>
          </div>

          <div className="col-14">
            <button type="submit" className="btn btn-primary">Submit</button>
          </div>
        </form>
      </div> */}

      <div className="container MoreInfoProfile">
        <form method=" ">
          <div className="row">
            <div className="col-md-4 mt-5 mb-3 mx-3">
              <img src="https://source.unsplash.com/user/erondu/300x300" className="img-rounded" alt="DP" />
            </div>

            <div className="col-md-6 mt-3">
              <div className="profile-head mt-3">
                <div className="row mb-3">
                  <div className="col">
                    <input type="text" className="form-control input-sm" placeholder="Your Name" aria-label="First name" />
                  </div>
                  <div className="col">
                    <input type="number" className="form-control input-sm" placeholder="Conatact Number" aria-label="Last name" />
                  </div>
                </div>
                <div className="row col-sm-6 mt-2">
                  <select className="form-select" aria-label="Default select example">
                    <option selected>Select Your Gender</option>
                    <option value="1">Male</option>
                    <option value="2">Female</option>
                    <option value="3">Other</option>
                  </select>
                </div>

                <div className="mb-3 mt-4 ">
                  <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Share a few lines about yourself "></textarea>
                </div>
                <div className="mb-3 mt-4 ">
                  <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Mention Some of your Qualities "></textarea>
                </div>

                <div className="col-14 mb-4 ">
                  <button type="submit" className="btn btn-dark btn-default">Submit</button>
                </div>

              </div>
            </div>
          </div>
        </form>
      </div>

    </>
  );
}
export default Moreinfo;

