import React from "react";
import {
  Card,
  CardHeader,
  CardTitle,
  Row,
  Col,
} from "reactstrap";

function History() {
  return (
    <div className="content">
      <Row>
        <Col xs="12">
          <Card>
            <CardHeader>
              <CardTitle tag="h4">History</CardTitle>
            </CardHeader>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default History;
