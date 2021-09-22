import './Navbar.css';
import { FaHome, FaBars } from "react-icons/fa";
import { Link } from 'react-router-dom'

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
              <Link className="nav-link active" aria-current="page" to="/">Home</Link>
            </li>
            <li class="nav-item">
              <Link className="nav-link active" aria-current="page" to="/Moreinfo">Profile</Link>
            </li>
            <li class="nav-item">
              <Link className="nav-link active" aria-current="page" to="/Matches">Matches</Link>
            </li>
            <li class="nav-item">
            <Link className="nav-link active" aria-current="page" to="/">Chats</Link>
            </li>
          </ul>
        </div>
      </nav>
    </section>
  );
}

export default Navbar;

