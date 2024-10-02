import React,{useState,useEffect} from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Legend, ResponsiveContainer } from 'recharts';
import {
    Card,
    CardHeader,
    CardBody,
    CardTitle,
    Row,
    Col
} from "reactstrap";


function Charts() {
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
 
const data = [
    { name: 'ci', uv:4000 , pv: 2400, amt: 2400 },
    { name: 'cnc', uv: 3000, pv: 1398, amt: 2210 },
    { name: 'cps', uv: 2000, pv: 9800, amt: 2290 },
    { name: 'cr', uv: 2780, pv: 3908, amt: 2000 },
    { name: 'cs', uv: 1890, pv: 4800, amt: 2181 },
    { name: 'ctc', uv: 2390, pv: 3800, amt: 2500 },
    { name: 'tw', uv: 3490, pv: 4300, amt: 2100 },
  ];
  return (
    <div className="content">
      <Row>
        <Col xs="12">
          <Card>
            <CardHeader>
              <CardTitle tag="h4">Total line of codes</CardTitle>
            </CardHeader>
            <CardBody>
              <ResponsiveContainer width="100%" height={400}>
                <BarChart data={data}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  {/* <Tooltip /> */}
                  <Legend />
                  <Bar dataKey="uv" fill="#8884d8" />
                  {/* You can add more Bar components for other data keys if needed */}
                </BarChart>
              </ResponsiveContainer>
            </CardBody>
          </Card>
        </Col>
        <Col xs="6">
          <Card>
            <CardHeader>
              <CardTitle tag="h4">ci</CardTitle>
            </CardHeader>
            <CardBody>
              <ResponsiveContainer width="100%" height={400}>
                <BarChart data={data}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  {/* <Tooltip /> */}
                  <Legend />
                  <Bar dataKey="uv" fill="#8884d8" />
                  {/* You can add more Bar components for other data keys if needed */}
                </BarChart>
              </ResponsiveContainer>
            </CardBody>
          </Card>
        </Col>
         <Col xs="6">
          <Card>
            <CardHeader>
              <CardTitle tag="h4">cnc</CardTitle>
            </CardHeader>
            <CardBody>
              <ResponsiveContainer width="100%" height={400}>
                <BarChart data={data}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  {/* <Tooltip /> */}
                  <Legend />
                  <Bar dataKey="uv" fill="#8884d8" />
                  {/* You can add more Bar components for other data keys if needed */}
                </BarChart>
              </ResponsiveContainer>
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default Charts;
