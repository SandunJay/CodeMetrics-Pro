import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie";

const Register = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      // Send email and password in the request body
      const response = await axios.post(
          "http://localhost:8085/api/v1/auth/register",
          null,
          {
            params: { email, password },
          }// Correctly pass email and password as an object
      );
      // Store the email in cookies for 2 hours
      Cookies.set("email", response.data.email, { expires: 1 / 12 });
      // Navigate to the dashboard after successful registration
      navigate("/admin/dashboard");
    } catch (err) {
      // Update the error message if needed
      setError("Invalid email or password."); // Consider updating this message based on the response
    }
  };

  return (
      <div>
        <h2>Register</h2>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <form onSubmit={handleRegister}>
          <div>
            <label>Email:</label>
            <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
            />
          </div>
          <div>
            <label>Password:</label>
            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />
          </div>
          <button type="submit">Register</button>
        </form>
      </div>
  );
};

export default Register;
