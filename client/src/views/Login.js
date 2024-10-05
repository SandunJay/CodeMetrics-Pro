import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import Cookies from "js-cookie";
import {
  Card,
  CardTitle,
  Form,
  FormGroup,
  Label,
  Input,
  Button,
  Alert,
} from "reactstrap";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8090/api/v1/auth/login",
        null,
        {
          params: { email, password },
        }
      );
      // Store the email in cookies for 2 hours
      Cookies.set("email", response.data.email, { expires: 1 / 12 });
      // Navigate to the dashboard after successful login
      navigate("/admin/dashboard");
    } catch (err) {
      setError("Invalid email or password.");
    }
  };

  // Inline styles for the component
  const styles = {
    body: {
      backgroundColor: "#343a40", // Dark background
      minHeight: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
    },
    card: {
      padding: '20px',
      textAlign: 'center',
      fontSize: '1.5rem',
      backgroundColor: '#495057',
      borderRadius: '0.5rem',
      boxShadow: '0 0 10px rgba(0, 0, 0, 0.5)',
      width: '400px',
    },
    header: {
      color: "#f8f9fa",
      marginBottom: "1rem",
    },
    label: {
      color: "#f8f9fa",
    },
    input: {
      backgroundColor: "#6c757d",
      color: "#f8f9fa",
    },
    button: {
      backgroundColor: "#007bff",
      border: "none",
      width: "100%",
      padding: "0.5rem",
      color: "white",
      marginBottom: "10px",
    },
    alert: {
      color: "#dc3545", // Danger alert color
    },
    registerLink: {
      color: "#007bff", // Color for the register link
      textDecoration: "none",
    },
  };

  return (
    <div style={styles.body}>
      <Card style={styles.card}>
        <CardTitle style={styles.header}>Login</CardTitle>
        {error && <Alert style={styles.alert}>{error}</Alert>}
        <Form onSubmit={handleLogin}>
          <FormGroup>
            <Label for="email" style={styles.label}>Email:</Label>
            <Input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={styles.input}
            />
          </FormGroup>
          <FormGroup>
            <Label for="password" style={styles.label}>Password:</Label>
            <Input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={styles.input}
            />
          </FormGroup>
          <Button type="submit" style={styles.button}>
            Login
          </Button>
        </Form>
        <p style={{ fontSize: '1.5rem' }}>
          Don't have an account?{" "}
          <Link to="/register" style={styles.registerLink}>
            Register here
          </Link>
        </p>
      </Card>
    </div>
  );
};

export default Login;
