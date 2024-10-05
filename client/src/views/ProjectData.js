import React,{useEffect,useState} from "react";
import classNames from "classnames";
import { Line, Bar } from "react-chartjs-2";

import {
  Button,
  ButtonGroup,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  UncontrolledDropdown,
  Label,
  FormGroup,
  Input,
  Table,
  Row,
  Col,
  UncontrolledTooltip,
} from "reactstrap";

import {
  chartExample1,
  chartExample2,
  chartExample3,
  chartExample4,
} from "variables/charts.js";
import Icons from "./Icons";
import { Await, useParams } from "react-router-dom";
import JSZip from 'jszip';
import { saveAs } from 'file-saver';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ProjectData = (props) => {
    const { id } = useParams(); 
    const [project, setProject] = useState(null);
    const [isOpen, setIsOpen] = useState(false);
    const [option, setOption] = useState('add');
    const [text, setText] = useState('');
    const [files, setFiles] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [bigChartData, setbigChartData] = React.useState("data1");
    const [isZipUpload, setIsZipUpload] = useState(false); 
const [responseData, setResponseData] = useState(null); 
  const setBgChartData = (name) => {
    setbigChartData(name);
  };
const [lineCounts,setLineCounts]=useState(0);
const [chartData,setChartData]=useState();
  const [totals, setTotals] = useState({
    ci: 0,
    cnc: 0,
    cps: 0,
    cr: 0,
    cs: 0,
    ctc: 0,
    tw: 0,
    singleLineCommentsCount: 0,
    multiLineCommentsCount: 0,
    totalComments:0
  });
  const [projects, setProjects] = useState([]);
  const [codeLines, setCodeLines] = useState('');
  const [loading, setLoading] = useState(false); 
  const [filteredLinesData, setFilteredLinesData] = useState([]);

  useEffect(() => {
    async function fetchProjectData() {
        setLoading(true); 
        try {
            const response = await fetch(`http://localhost:8090/api/v1/project/${id}`);
            const data = await response.json();
            setProject(data);
            setLoading(false);
        } catch (error) {
            setLoading(false); 
            console.error("Error fetching project data:", error);
        }
    }

    fetchProjectData();
  }, [id]);

  useEffect(() => {
    if (project && project.files && project.files.length > 0) {
      let totalSingleLineComments = 0;
      let totalMultiLineComments = 0;
      let totalCps = 0;
      let totalCs = 0;
      let totalLines = 0;
        let totalCi = 0;
        let totalCnc = 0;
        let totalCr = 0;
        let totalCtc = 0;
        let totalTw = 0;
        let totalSingleLineCommentsCount = 0;
        let totalMultiLineCommentsCount = 0;
        let totalTotalComments = 0;



      const filteredData = project.files.flatMap(file => 
        file.linesData.filter(line => {
          if (line.cs >= 1) {
            totalCps += line.cps;
            totalCs += line.cs;


            totalCi += line.ci || 0;
            totalCnc += line.cnc || 0;
          totalCps += line.cps || 0;
          totalCr += line.cr || 0;
          totalCs += line.cs || 0;
          totalCtc += line.ctc || 0;
          totalTw += line.tw || 0;
          totalSingleLineCommentsCount+=line.singleLineCommentsCount||0;
          totalMultiLineCommentsCount+=line.multiLineCommentsCount||0;
          totalTotalComments+=line.singleLineCommentsCount+line.multiLineCommentsCount||0;

            totalLines += 1;
            return true;
          }
          return false;
        })
      );

      setFilteredLinesData(filteredData);
      setTotals({
        singleLineCommentsCount: totalSingleLineCommentsCount,
        multiLineCommentsCount: totalMultiLineCommentsCount,
        cps: totalCps,
        cs: totalCs,
        ci: totalCi,
        cnc: totalCnc,
        cr: totalCr,
        ctc: totalCtc,
        tw: totalTw,
        totalComments: totalTotalComments,
        totalLines,
      });
    }
  }, [project]);

  if (!project) {
    return <div>Loading...</div>; 
  }

  const getChartData = (canvas) => {
    const chartFunction = chartExample1[bigChartData];
    return chartFunction ? chartFunction(canvas, lineCounts) : {};
  };

  const toggleModal = () => setIsOpen(!isOpen);

  const handleFilePicker = (event) => {
    const selectedFiles = Array.from(event.target.files);
    setFiles(selectedFiles);

    if (selectedFiles.length === 1) {
      setSelectedFile(selectedFiles[0]);
    }
  };

  const handleTextSubmit = async () => {
    try {
      const response = await fetch('http://localhost:8090/api/v1/detect/text', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code: text }),
      });

      toggleModal(); 
      handleToast(response.ok ? 'success' : 'fail');
    } catch (error) {
      toggleModal(); 
      handleToast('fail');
    }
  };

  const handleZipAndUpload = async () => {
    const zip = new JSZip();
    files.forEach((file) => {
      if (!file.webkitRelativePath.includes('node_modules') && !file.webkitRelativePath.includes('.git') &&
        !file.webkitRelativePath.includes('.idea') && !file.webkitRelativePath.includes('.mvn')) {
        zip.file(file.webkitRelativePath, file);
      }
    });

    const content = await zip.generateAsync({ type: 'blob' });
    const formData = new FormData();
    formData.append('zipFile', content, 'files.zip');
    formData.append('projectId', id);

    setLoading(true); 

    try {
      console.error('uploading file');
      const response = await fetch('http://localhost:8090/api/v1/detect/zip', {
        method: 'POST',
        body: formData,
      });

      setLoading(false);
      if (response.ok) {
        setIsZipUpload(true); 
        const data = await response.json();
        setProject(data); 
      } else {
        setProject(null); 
        console.error('Error uploading zip:', response.statusText);
      }

      toggleModal(); 
      handleToast(response.ok ? 'success' : 'fail');
    } catch (error) {
        setLoading(false); 
        setProject(null); 
        console.error('Error uploading zip:', error);
        toggleModal();
        handleToast('fail');
    }
  };

  const handleFileUpload = async () => {
    const formData = new FormData();
    formData.append('classFile', selectedFile);

    try {
      console.error('uploading file');
      const response = await fetch('http://localhost:8090/api/v1/detect/file', {
        method: 'POST',
        body: formData,
      });
      // if (response.ok) {
      //   console.log('File uploaded successfully');
      // } else {
      //   console.error('Error uploading file:', response.statusText);
      // }
      toggleModal(); // Close the modal upon success
      handleToast(response.ok ? 'success' : 'fail');
    } catch (error) {
      // console.error('Error uploading file:', error);
      toggleModal(); // Close the modal even if there's an error
      handleToast('fail');
    }
  };


  const handleToast = (status) => {
    if (status === 'success') {
      toast.success('Upload successful!', {
        position: "top-right",
        autoClose: 3000, // 5 seconds
        style: { backgroundColor: 'white', color: 'green' }, // Green toast for success
      });
    } else {
      toast.error('Upload failed!', {
        position: "top-right",
        autoClose: 3000, // 5 seconds
        style: { backgroundColor: 'white', color: 'red' }, // Red toast for failure
      });
    }
  };
  
  // Add this to ensure toast notifications are shown
  toast.apply();


  const renderSourceCodeOrName = () => {
    if (!project) return null; // Wait for project data

    if (project.cp === 0 || project.cp === null) {
      return (
        <CardTitle tag="h2" style={{ textDecoration: 'underline' }}>
          Source Code
          <div>
            <Button color="primary" onClick={toggleModal}>Add</Button>
            <Modal isOpen={isOpen} toggle={toggleModal} size="lg">
              <ModalHeader toggle={toggleModal}>Source Code</ModalHeader>
              <ModalBody>
                {/* Options to select */}
                <div>
                  <Button color={option === 'add' ? 'primary' : 'secondary'} onClick={() => setOption('add')}>Add</Button>
                  <Button color={option === 'file' ? 'primary' : 'secondary'} onClick={() => setOption('file')}>Pick File</Button>
                  <Button color={option === 'directory' ? 'primary' : 'secondary'} onClick={() => setOption('directory')}>Pick Directory</Button>
                </div>
                <hr />
                {option === 'add' && (
                  <Input
                    type="textarea"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    rows="10"
                    placeholder="Enter source code ..."
                    style={{ color: 'black', backgroundColor: 'white' }}
                  />
                )}
                {option === 'file' && (
                  <div>
                    <input type="file" onChange={handleFilePicker} />
                    {selectedFile && <p>Selected File: {selectedFile.name}</p>}
                  </div>
                )}
                {option === 'directory' && (
                  <div>
                    <input type="file" webkitdirectory="true" directory="" multiple onChange={handleFilePicker} />
                    <ul>
                      {files.slice(0, 20).map((file, index) => (
                        <li key={index} style={{ color: 'gray' }}>{file.webkitRelativePath}</li>
                      ))}
                      {files.length > 20 && <li style={{ color: 'gray' }}>... and {files.length - 20} more</li>}
                    </ul>
                  </div>
                )}
              </ModalBody>
                <ModalFooter>
                    {option === 'add' && (
                        <Button color="primary" onClick={handleTextSubmit} className="p-2 ml-3 mb-2">
                        Submit
                        </Button>
                    )}

                    {option === 'file' && (
                        <Button color="primary" onClick={handleFileUpload} className="p-2 ml-3 mb-2">
                        Upload File
                        </Button>
                    )}

                    {option === 'directory' && (
                        <Button color="primary" onClick={handleZipAndUpload} className="p-2 ml-3 mb-2">
                        Upload Zipped Directory
                        </Button>
                    )}

                    <Button color="secondary" onClick={toggleModal} className="p-2 mr-3 mb-2">
                        Cancel
                    </Button>
                </ModalFooter>
            </Modal>
          </div>
        </CardTitle>
      );
    } else {
      return (
        <CardTitle tag="h2" style={{ textDecoration: 'underline' }}>{project.name}</CardTitle>
      );
    }
  };

  return (
    <>
      <div className="content">

            <ToastContainer />
            <Row>
              <Col xs="12">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  {renderSourceCodeOrName()}
                </Card>
              </Col>
              {loading ? (
                <div>Loading...</div>
                ) : project ? (
                <>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Total Lines Of Code</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{lineCounts}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Index</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.ci}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' ,height: '175px' }}>
                  <CardTitle tag="h2">Design Patterns</CardTitle>
                  <div style={{ fontSize: '1rem', fontWeight: 'bold', color: 'white', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    {project.patterns.map((pattern, index) => (
                      <div key={index}>{pattern}</div>
                    ))}
                  </div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Complexity</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.cnc}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Performance Score</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.cps}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Review</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.cr}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Size</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.cs}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Code Type Classification</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.ctc}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Text Width</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.tw}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Total Comments</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.totalComments}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Single line Comments</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.singleLineCommentsCount}</div>
                </Card>
              </Col>
              <Col xs="4">
                <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                  <h5 className="card-category"></h5>
                  <CardTitle tag="h2">Multi line Comments</CardTitle>
                  <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'white' }}>{totals.multiLineCommentsCount}</div>
                </Card>
              </Col>
              </>
                ) : (
                <div>No Data Available</div>
                )}
            </Row>
            <Row>
            {isZipUpload && (
                <>
                  <Col xs="6">
                    <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                      <CardTitle tag="h2">Overview</CardTitle>
                      <div style={{ fontSize: '1.2rem', color: 'white' }}>
                        {responseData?.overview || 'No overview available'} {/* Check if responseData exists, otherwise display a fallback message */}
                      </div>
                    </Card>
                  </Col>
                  <Col xs="6">
                    <Card style={{ padding: '20px', textAlign: 'center', fontSize: '1.5rem' }}>
                      <CardTitle tag="h2">Improved Code</CardTitle>
                      <div style={{ fontSize: '1.2rem', color: 'white' }}>
                        {responseData?.improvedCode || 'No improved code available'} {/* Check if responseData exists, otherwise display a fallback message */}
                      </div>
                    </Card>
                  </Col>
                </>
              )}

            </Row>
            <Row>
              <Col xs="12">
                <Table striped>
                  <thead>
                    <tr>
                      <th>Line No</th>
                      <th>Data</th>
                      <th>Code Size (cs)</th>
                      <th>Single Line Comments</th>
                      <th>Multi Line Comments</th>
                      <th>Code Performance Score</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredLinesData.map((line, index) => (
                      <tr key={index}>
                        <td>{line.lineNo}</td>
                        <td>{line.data}</td>
                        <td>{line.cs}</td>
                        <td>{line.singleLineCommentsCount}</td>
                        <td>{line.multiLineCommentsCount}</td>
                        <td>{line.cps}</td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Col>
            </Row>
      </div>
    </>
  );
  
}

export default ProjectData;
