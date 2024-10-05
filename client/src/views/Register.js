import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
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

const Register = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [username, setUsername] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError(""); // Clear previous errors
    setSuccess(""); // Clear previous success messages

    try {
      const requestBody = {
        email,
        password,
        username,
      };

      const response = await axios.post(
        "http://localhost:8090/api/v1/auth/register",
        requestBody
      );

      // Handle successful registration (optional success message)
      setSuccess("Registration successful! You can now log in.");
      // Optionally navigate to the login page
      // navigate("/login");
    } catch (err) {
      setError("Registration failed. Please try again.");
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
    success: {
      color: "#28a745", // Success alert color
    },
    loginLink: {
      color: "#007bff", // Color for the login link
      textDecoration: "none",
    },
  };

  return (
    <div style={styles.body}>
      <Card style={styles.card}>
        <CardTitle style={styles.header}>Register</CardTitle>
        {error && <Alert style={styles.alert}>{error}</Alert>}
        {success && <Alert style={styles.success}>{success}</Alert>}
        <Form onSubmit={handleRegister}>
          <FormGroup>
            <Label for="username" style={styles.label}>Username:</Label>
            <Input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              style={styles.input}
            />
          </FormGroup>
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
            Register
          </Button>
        </Form>
        <p style={{ fontSize: '1.5rem' }}>
          Already have an account?{" "}
          <Link to="/login" style={styles.loginLink}>
            Login here
          </Link>
        </p>
      </Card>
    </div>
  );
};

export default Register;
