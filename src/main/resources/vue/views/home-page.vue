<!-- the "home-page" element is passed as a parameter to VueComponent in the JavalinConfig file -->
<!--suppress ALL -->
<template id="home-page">
  <app-layout>
    <div class="row">
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
            <a href="/activities" class="btn btn-primary">More Details...</a>
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
      }
    });
</script>