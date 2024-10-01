/*!

=========================================================
* Black Dashboard React v1.2.2
=========================================================

* Product Page: https://www.creative-tim.com/product/black-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/black-dashboard-react/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import React,{useEffect,useState} from "react";
// nodejs library that concatenates classes
import classNames from "classnames";
// react plugin used to create charts
import { Line, Bar } from "react-chartjs-2";

// reactstrap components
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

// core components
import {
  chartExample1,
  chartExample2,
  chartExample3,
  chartExample4,
} from "variables/charts.js";
import Icons from "./Icons";
import { Await } from "react-router-dom";
import JSZip from 'jszip';
import { saveAs } from 'file-saver';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function Dashboard(props) {

  const [bigChartData, setbigChartData] = React.useState("data1");
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

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await fetch('http://localhost:8090/projects');
        const data = await response.json();

        console.log(data[0]?.files[0]); // Log the first file for debugging

        // Update chart data and projects data
        setChartData(data);
        setProjects(data);

        // Calculate line count
        const linesData = data[0]?.files[0]?.linesData || [];
        const lineCount = linesData.length;
        setLineCounts(lineCount);
        console.log(`Total number of lines: ${lineCount}`); // Log line count

        // Calculate totals for each metric
        const newTotals = {
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
        };

        linesData.forEach(line => {
          newTotals.ci += line.ci || 0;
          newTotals.cnc += line.cnc || 0;
          newTotals.cps += line.cps || 0;
          newTotals.cr += line.cr || 0;
          newTotals.cs += line.cs || 0;
          newTotals.ctc += line.ctc || 0;
          newTotals.tw += line.tw || 0;
          newTotals.singleLineCommentsCount+=line.singleLineCommentsCount||0;
          newTotals.multiLineCommentsCount+=line.multiLineCommentsCount||0;
          newTotals.totalComments+=line.singleLineCommentsCount+line.multiLineCommentsCount||0;
        });

        setTotals(newTotals); // Update totals state
        const formattedCode = linesData.map(line => line.data).join('\n');
        setCodeLines(formattedCode);
      } catch (error) {
        console.error('There was a problem with the fetch operation:', error);
      }
    }

    fetchData();
  }, []);
  const getChartData = (canvas) => {
    const chartFunction = chartExample1[bigChartData];
    return chartFunction ? chartFunction(canvas, lineCounts) : {};
  };


  // const [files, setFiles] = useState([]);

  // const handleFilePicker = async (event) => {
  //   const selectedFiles = event.target.files;
  //   const fileArray = Array.from(selectedFiles);
  //   setFiles(fileArray);

  //   if (fileArray.length === 1) {
  //     // If there's only one file, no need to zip, just handle it
  //     console.log('Selected single file:', fileArray[0].name);
  //   } else {
  //     // Multiple files, create zip
  //     const zip = new JSZip();
  //     fileArray.forEach((file) => {
  //       zip.file(file.webkitRelativePath, file);
  //     });

  //     const content = await zip.generateAsync({ type: 'blob' });
  //     saveAs(content, 'files.zip');
  //   }
  // };

  const [isOpen, setIsOpen] = useState(false);
  const [option, setOption] = useState('add'); // Initially show Add
  const [text, setText] = useState(''); // Text input for Add option
  const [files, setFiles] = useState([]); // For file/directory picking
  const [selectedFile, setSelectedFile] = useState(null); // For single file picking

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
      const response = await fetch('http://localhost:8085/api/v1/detect/text', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code: text }),
      });

      toggleModal(); // Close the modal upon success
      handleToast(response.ok ? 'success' : 'fail');
    } catch (error) {
      toggleModal(); // Close the modal even if there's an error
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
    // Upload the zip to the backend
    const formData = new FormData();
    formData.append('zipFile', content, 'files.zip');

    try {
      console.error('uploading file');
      const response = await fetch('http://localhost:8085/api/v1/detect/zip', {
        method: 'POST',
        body: formData,
      });

      toggleModal(); // Close the modal upon success
      handleToast(response.ok ? 'success' : 'fail');

      // if (response.ok) {
      //   console.log('Zip uploaded successfully');
      // } else {
      //   console.error('Error uploading zip:', response.statusText);
      // }
    } catch (error) {
      // console.error('Error uploading zip:', error);
      toggleModal(); // Close the modal even if there's an error
      handleToast('fail');
    }
  };

  const handleFileUpload = async () => {
    const formData = new FormData();
    formData.append('classFile', selectedFile);

    try {
      console.error('uploading file');
      const response = await fetch('http://localhost:8085/api/v1/detect/file', {
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

  return (
    <>
      <div className="content">
      <ToastContainer />
        <Row>
          <Col xs="12">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <div>
                <CardTitle tag="h2" style={{ textDecoration: 'underline' }}>
                  Source Code
                  
                  {/* <input
                    type="file"
                    webkitdirectory="true"
                    directory=""
                    multiple
                    onChange={handleFilePicker}
                  /> */}

                  <div>
                  <Button color="primary" onClick={toggleModal}>
                      Image
                    </Button>

                    <Button color="primary" onClick={toggleModal}>
                      Add
                    </Button>

                    <Modal isOpen={isOpen} toggle={toggleModal} size="lg">
                      <ModalHeader toggle={toggleModal}>Source Code</ModalHeader>
                      <ModalBody>
                        {/* Options to select */}
                        <div>
                          <Button color={option === 'add' ? 'primary' : 'secondary'} onClick={() => setOption('add')}>
                            Add
                          </Button>
                          <Button color={option === 'file' ? 'primary' : 'secondary'} onClick={() => setOption('file')}>
                            Pick File
                          </Button>
                          <Button color={option === 'directory' ? 'primary' : 'secondary'} onClick={() => setOption('directory')}>
                            Pick Directory
                          </Button>
                        </div>

                        <hr />

                        {/* Dynamic Content Based on Selection */}
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
                            <div>
                              <h5 style={{ color: 'black' }}>Selected Files</h5>
                              <ul>
                                {files.slice(0, 20).map((file, index) => (
                                  <li key={index} style={{ color: 'gray' }}>{file.webkitRelativePath}</li>
                                ))}
                                {files.length > 20 && <li style={{ color: 'gray' }}>... and {files.length - 20} more</li>}
                              </ul>
                            </div>
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

              </div>
              <div style={{fontSize: '1rem', fontWeight: 'bold', color: 'white'}}>
                <div>
                  <pre style={{whiteSpace: 'pre-wrap', wordBreak: 'break-all'}}>
          <code>{codeLines}</code>
        </pre>
                </div>
              </div>
            </Card>

          </Col>
          <Col xs="8">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Total Lines Of Code</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{lineCounts}</div>
            </Card>

          </Col>
          <Col xs="4">
            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Index</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.ci}</div>
            </Card>
          </Col>
          <Col xs="4">
            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Design Patterns</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>Design Pattern list</div>
            </Card>
          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Complexity</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.cnc}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Performance Score</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.cps}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Review</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.cr}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Size</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.cs}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Code Type Classification</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.ctc}</div>
            </Card>

          </Col>

          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Text Width</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.tw}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Total Comments</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.totalComments}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2">Single line Comments</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.singleLineCommentsCount}</div>
            </Card>

          </Col>
          <Col xs="4">

            <Card style={{padding: '20px', textAlign: 'center', fontSize: '1.5rem'}}>
              <h5 className="card-category"></h5>
              <CardTitle tag="h2"> Multi line Comments</CardTitle>
              <div style={{fontSize: '3rem', fontWeight: 'bold',color: 'white'}}>{totals.multiLineCommentsCount}</div>
            </Card>

          </Col>

        </Row>
      </div>
    </>
  );
}

export default Dashboard;
