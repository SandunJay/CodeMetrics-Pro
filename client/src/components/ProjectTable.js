import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Row, Col, Pagination, PaginationItem, PaginationLink } from 'reactstrap';
import axios from 'axios';

const ProjectTable = ({ refreshTable }) => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [hasMoreData, setHasMoreData] = useState(true);
  const projectsPerPage = 5;
  const navigate = useNavigate();

  const fetchProjects = async () => {
    try {
      const response = await axios.get(`http://localhost:8085/api/v1/project?page=${page}&limit=${projectsPerPage}`);
      if (response.data.length === 0) setHasMoreData(false);
      else setProjects(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching projects:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, [page, refreshTable]);

  const getTotalLines = (files) => {
    return files ? files.reduce((acc, file) => acc + file.linesData.length, 0) : 0;
  };

  const viewMoreDetails = (id) => {
    navigate(`/admin/project/${id}`);
  };

  const deleteProject = async (id) => {
    try {
      const response = await axios.delete(`http://localhost:8085/api/v1/project/${id}`);
      if (response.data.length === 0) setHasMoreData(false);
      else setProjects(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error deleting project:", error);
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  return (
    <>
      {loading ? (
        <div>Loading...</div>
      ) : (
        <>
          <Table responsive dark className="table-no-scroll"> {/* Custom CSS class to remove scrollbars */}
            <thead className="text-primary">
              <tr>
                <th>Project Name</th>
                <th>Language</th>
                <th>Total Lines</th>
                <th>CP</th>
                <th className="text-center">Actions</th> {/* Center the header */}
              </tr>
            </thead>
            <tbody>
              {projects.map((project) => (
                <tr key={project.id}>
                  <td>{project.name}</td>
                  <td>{project.language}</td>
                  <td>{getTotalLines(project.files)}</td>
                  <td>{project.cp}</td>
                  <td className="text-center"> {/* Center the buttons */}
                    <Button
                      color="info"
                      size="sm"
                      onClick={() => viewMoreDetails(project.id)}
                      className="me-3" // Add space after the button
                    >
                      View More
                    </Button>
                    <Button
                      color="danger"
                      size="sm"
                      onClick={() => deleteProject(project.id)}
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>

          {!hasMoreData && (
            <div className="text-center text-muted">No more data available</div>
          )}

          {/* Center pagination */}
          <Row className="justify-content-center">
            <Col md="auto">
              <Pagination aria-label="Project pagination">
                <PaginationItem disabled={page === 1}>
                  <PaginationLink previous onClick={() => handlePageChange(page - 1)} />
                </PaginationItem>
                <PaginationItem active>
                  <PaginationLink>{page}</PaginationLink>
                </PaginationItem>
                {hasMoreData && (
                  <PaginationItem>
                    <PaginationLink onClick={() => handlePageChange(page + 1)}>
                      {page + 1}
                    </PaginationLink>
                  </PaginationItem>
                )}
                <PaginationItem disabled={!hasMoreData}>
                  <PaginationLink next onClick={() => handlePageChange(page + 1)} />
                </PaginationItem>
              </Pagination>
            </Col>
          </Row>
        </>
      )}
    </>
  );
};

export default ProjectTable;
