
<!--suppress JSAnnotator -->
<template id="user-login">
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">User Login</h2>
      <form>
        <div class="form-group">
          <label for="email">email</label>
          <input
              type="email"
              id="email"
              v-model="loginData.email"
              placeholder="enter email"
              required
          />
        </div>
        <div class="form-group">
          <label for="password">password</label>
          <input
              type="password"
              id="password"
              v-model="loginData.password"
              placeholder="enter password"
              required
          />
        </div>
        <button type="button" class="login-button" @click="sendLoginRequest">Login</button>
      </form>
    </div>
  </div>
</template>

<script>
app.component("user-login", {
  template: "#user-login",
  data: () => ({
    loginData: {
      email: "",
      password: "",
    },
  }),
  methods: {
    sendLoginRequest() {
      axios.post("/api/users/login", this.loginData)
          .then(response => {
            window.location.href = "/"; // Redirect to the homepage
          })
          .catch(error => {
            this.errorMessage = error.response?.data;
          });
    },
  },
});
</script>


<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url('https://example.com/background.jpg') no-repeat center center;
  background-size: cover;
}

.login-card {
  background-color: rgba(255, 255, 255, 0.9);
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 300px;
}

.login-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}

.form-group input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.login-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4a90e2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.login-button:hover {
  background-color: #357ab8;
}

.error-message {
  color: red;
  margin-top: 1rem;
  font-size: 0.9rem;
}
</style>