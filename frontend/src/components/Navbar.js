import './Navbar.css';
import { FaHome, FaBars } from "react-icons/fa";

function Navbar(props) {
  
  return (
    <section id="nav-bar">
      <nav class="navbar navbar-expand-lg navbar-light">
        <button className="navbar-toggler white" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <FaBars />
          {/* <i class="fa fa-bars"></i> */}
        </button>
        <a class="navbar-brand" href="/#"><img src="images/ffds_logo.png" alt="blah" id="home" /></a>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ml-auto">
            <li class="nav-item">
              <FaHome className="iconhome" size="40px" color="white"  style={{ padding: "0px 0px  10px 0px" }} />
              <a class="nav-link " aria-current="page" href="/#home">Home</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/#signup" onClick={() => props.toggle(false)}>Sign up</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/#login" onClick={() => props.toggle(true)}>Login</a>
            </li>
          </ul>
        </div>
      </nav>
    </section>
  );
}

export default Navbar;

