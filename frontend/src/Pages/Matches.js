import React from "react";
import Carousel from "react-bootstrap/Carousel";
import { Modal,Button } from "react-bootstrap";
import "./Matches.css";
function MyVerticallyCenteredModal(props) {
    return (
      <Modal 
        {...props}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Modal heading
          </Modal.Title>
        </Modal.Header>
        <Modal.Body >
          <h4>Centered Modal</h4>
          <p>
            Cras mattis consectetur purus sit amet fermentum. Cras justo odio
            dapibus ac facilisis in, egestas eget quam. Morbi leo risus, port
            consectetur ac, vestibulum at eros.ksssssssssssssssssssssssssssss
            sjdksbdvdbsdmjbshbdshbdjsndjsndm,sbdhsdsdnvsdshd,ms,vmnam,vdnsmhs
            abhiramsuarhdklsndjsbhdksdbhjdbdhvhsavdhgsvdhsvdhdsbhjbdjsbdjbsdm
            shdhjahjdmjsdh.asdjkasdhsuarabhiramksamdjabd,masdjsbmnabmnabmnabn
            mbjsdkfhnbsbadbsjbdsmabdjsbsbdmjsbdjdm,bdsvdnbsvdbnsvdmvdhmsdnmsd
            skhhjasdjasdjsahdhjsvdj,sbdvgsvdj,sbdhsvdhndhnsvdhmsdvhsvdhjsdvhs
            sbdsbdmsbdmnsbdnmsvdbndvbnsdvdbndvdbnvsbnd vbnsddvhddcsdb dbsnvhh
            sjdsbjksdjsdjmsbdmsbfjksdhfjkdbjsdb,masbmnsd mnscnmsc dn,asdklsdm
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={props.onHide} variant="success">Accept </Button>
          <Button onClick={props.onHide} variant="danger">Reject</Button>
        </Modal.Footer>
      </Modal>
    );
  }
const Matches = () => {
    const [modalShow, setModalShow] = React.useState(false);
  return (
    <div>
      <Carousel>
        <Carousel.Item  onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="/images/BLACK.jpg"
            alt="First slide"
          />
          
        </Carousel.Item >
        <Carousel.Item onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="/images/BLACK.jpg"
            alt="Second slide"
          />

          
        </Carousel.Item>
        <Carousel.Item onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="/images/BLACK.jpg"
            alt="Third slide"
          />

          
        </Carousel.Item>
      </Carousel>
      <MyVerticallyCenteredModal
        show={modalShow}
        onHide={() => setModalShow(false)}
      />
    </div>
    
  );
};

export default Matches;
