<!-- the "home-page" element is passed as a parameter to VueComponent in the JavalinConfig file -->
<!--suppress ALL -->
<template id="home-page">
  <app-layout>
    <div class="row mt-4">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Registered Users</h5>
          <div class="card-body">
            <h5 class="card-title">{{users.length}} users</h5>
            <a href="/users" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Total Activities</h5>
          <div class="card-body">
            <h5 class="card-title">{{activities.length}} activities</h5>
            <a href="/activities" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
    </div>

    <!-- new row for cards -->
    <div class="row mt-4">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Water Intake</h5>
          <div class="card-body">
            <h5 class="card-title">{{waterIntakes.length}} records</h5>
            <a href="/waterintake" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Calorie Log</h5>
          <div class="card-body">
            <h5 class="card-title">{{calorieLogs.length}} logs</h5>
            <a href="/calorielogs" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
    </div>

    <!-- change font size card -->
    <div class="row mt-4">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Small Font</h5>
          <div class="card-body">
            <h5 class="card-title">Detail content</h5>
            <button class="btn btn-primary" @click="setFontSize('small')">Apply</button>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Medium Font</h5>
          <div class="card-body">
            <h5 class="card-title">Default content</h5>
            <button class="btn btn-primary" @click="setFontSize('medium')">Apply</button>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Large Font</h5>
          <div class="card-body">
            <h5 class="card-title">Important information</h5>
            <button class="btn btn-primary" @click="setFontSize('large')">Apply</button>
          </div>
        </div>
      </div>
    </div>

  </app-layout>
</template>

<script>
app.component('home-page',
    {
      template: "#home-page",
      data: () => ({
        users: [],
        activities: [],
        waterIntakes: [],
        calorieLogs: [],
      }),
      created() {
        axios.get("/api/users")
            .then(res => this.users = res.data)
            .catch(() => alert("Error while fetching users"));
        axios.get("/api/activities")
            .then(res => this.activities = res.data)
            .catch(() => alert("Error while fetching activities"));
        axios.get("/api/waterIntakes/user/2")
            .then(res => this.waterIntakes = res.data.waterIntakes)
            .catch(() => alert("Error while fetching records"));
        axios.get("/api/calories/user/2")
            .then(res => this.calorieLogs = res.data.calorieLogs)
            .catch(() => alert("Error while fetching records"));
      },
      methods: {
        setFontSize(size) {
          const root = document.documentElement;
          root.style.setProperty('--font-size', size === 'small' ? '16px' : size === 'medium' ? '20px' : '24px');
        },
      },
    });
</script>

<style scoped>
.card-title {
  font-size: var(--font-size, 16px);
}
</style>