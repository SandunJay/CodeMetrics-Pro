import axios from 'axios';
import ProjectTable from 'components/ProjectTable';
import Cookies from 'js-cookie';
import React, { useState, useEffect } from 'react';
import { Card, CardHeader, CardBody, CardTitle, Row, Col, Button, Modal, ModalHeader, ModalBody, Form, FormGroup, Label, Input, ModalFooter } from 'reactstrap';

const DashboardTable = () => {
  const [modalOpen, setModalOpen] = useState(false);
  const [projectName, setProjectName] = useState('');
  const [loading, setLoading] = useState(false);
  const [refreshTable, setRefreshTable] = useState(false); // To refresh the table

  const toggleModal = () => {
    setModalOpen(!modalOpen);
    setProjectName(''); // Reset the input field when closing
  };

  const handleNewProject = async () => {
    try {
      setLoading(true);
      const email = Cookies.get('email'); // Get the email from the cookie
      if (!email) {
        alert('No email found in the cookie!');
        setLoading(false);
        return;
      }
      const response = await axios.post('http://localhost:8085/api/v1/project/create', {
        name: projectName,
        email: email,
      });
      console.log('Project created:', response.data);
      setLoading(false);
      toggleModal(); // Close the modal after successful submission
      setRefreshTable(!refreshTable); // Trigger table refresh
    } catch (error) {
      console.error('Error creating project:', error);
      setLoading(false);
    }
  };

  return (
    <div className="content">
      <Row>
        <Col md="12">
          <Card>
            <CardHeader>
              <div className="d-flex justify-content-between align-items-center">
                <CardTitle tag="h4" className="text-white">All Projects</CardTitle>
                <Button color="success" onClick={toggleModal}>
                  Add New Project
                </Button>
              </div>
            </CardHeader>
            <CardBody>
              {/* Pass refreshTable to trigger table data refresh */}
              <ProjectTable refreshTable={refreshTable} />
            </CardBody>

            {/* Modal for adding a new project */}
            <Modal isOpen={modalOpen} toggle={toggleModal} centered  style={{ color: '#fff' }}>
              <ModalHeader toggle={toggleModal}  style={{ backgroundColor: '#344675', color: '#fff', zIndex:'1' }}>Create New Project</ModalHeader>
              <ModalBody style={{ backgroundColor: '#344675' }}> {/* Inherit background color */}
                <Form>
                  <FormGroup>
                    <Label for="projectName">Project Name</Label>
                    <Input
                      type="text"
                      id="projectName"
                      value={projectName}
                      onChange={(e) => setProjectName(e.target.value)}
                      placeholder="Enter project name"
                      style={{ backgroundColor: '#344675', color: '#fff' }} // Inherit background color
                    />
                  </FormGroup>
                </Form>
              </ModalBody>
              <ModalFooter  style={{ backgroundColor: '#344675' }}>
                <Button color="secondary" onClick={toggleModal}>
                  Cancel
                </Button>
                <Button color="primary" onClick={handleNewProject} disabled={loading || !projectName}>
                  {loading ? 'Submitting...' : 'Submit'}
                </Button>
              </ModalFooter>
            </Modal>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardTable;
