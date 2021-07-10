import'./Navbar.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {FaHome} from "react-icons/fa";
import {FaBars} from "react-icons/fa";

function Navbar()
{
    return(
       

        <section id="nav-bar">
        <nav class="navbar navbar-expand-lg navbar-light ">
            <div class="container-fluid">
              <FaBars className="iconbar" size=" 50px" color="white"/>
              <a class="navbar-brand" href="/#"><img src="images/ffds_logo.png" alt="blah" id="home" /></a>
              <button class="navbar-toggler" type="button"  data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav"  aria-expanded="false" aria-label="Toggle navigation">
                
                <i class="fa fa-bars" ></i>

              </button>
              <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav  ml-auto">
                  <li class="nav-item">
                 
                  <FaHome className="iconhome" size="40px" color="white"/>  <a class="nav-link " aria-current="page" href="/#home">Home</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" href="/#signup">Sign up</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" href="/#login">LOGIN</a>
                  </li>
                 
                </ul>
              </div>
            </div>
          </nav>
    </section>
    );
    }
export default Navbar;
        
