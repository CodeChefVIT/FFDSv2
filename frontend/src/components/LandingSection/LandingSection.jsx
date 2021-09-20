import "./LandingSection.css";
import phone from "../LandingSection/phone.svg";
import heart from "../LandingSection/heart.svg";
import logo from "../LandingSection/ffdslogo.svg";
import home from "../LandingSection/homelogo.svg";
import started from "../LandingSection/started.svg";
import question from "../LandingSection/question.svg";
import hands from "../LandingSection/hands.svg";
import insta from "../LandingSection/2.svg";
import fb from "../LandingSection/3.svg";

import {Link} from "react-router-dom";

const LandingSection = () => {

  return (
    <section id="landing">
      <main className="landing">
        <article>
          <h2 className="main-heading">
            FULLY FLEXIBLE <span className="purple">Dating </span> System
          </h2>
          <h3 className="sub-heading">
            <img src={heart} class="main-heart"></img>
          </h3>
        </article>
        <img src={phone} class="landingsvg"></img>
      </main>
      <main className="highlights">
        <div className="highlight1">The best dating site</div>
        <div className="highlight2">Meet new people</div>
        
        <div className="highlight3">Make trustful relations</div>
      </main>
      <main className="started">
        <h2 className="started-title">How To Use FFDS ?</h2>
        <Link className="button" to="/Moreinfo">Get started</Link>
        <article className="started-main">
          <h2 className="started-content">
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.
          </h2>
          <img src={started} class="startedsvg"></img>
        </article>
      </main>
      <main className="question">
        <article className="question-main">
          <img src={question} class="questionsvg"></img>
          <h2 className="question-content">
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.
          </h2>
        </article>
      </main>
      <main className="hands">
        <article className="hands-main">
          <h2 className="hands-content">
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.<br></br>
            Lorem ipsum dolor sit amet.
          </h2>
          <img src={hands} class="handssvg"></img>
        </article>
        <Link className="button" to="/Moreinfo">Get started</Link>
      </main>
    </section>
  );
};

export default LandingSection;
