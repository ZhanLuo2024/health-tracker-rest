<!--suppress JSAnnotator -->
<template id="user-activity-overview">
  <app-layout>
    <div class="container mt-4">
      <h2>User Activities</h2>
      <table class="table table-striped">
        <thead>
        <tr>
          <th>#</th>
          <th>Description</th>
          <th>Duration (minutes)</th>
          <th>Calories</th>
          <th>Started</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(activity, index) in activities" :key="activity.id">
          <td>{{ index + 1 }}</td>
          <td>{{ activity.description }}</td>
          <td>{{ activity.duration }}</td>
          <td>{{ activity.calories }}</td>
          <td>{{ formatDate(activity.started) }}</td>
          <td>
            <button class="btn btn-primary btn-sm" @click="viewActivity(activity.id)">View</button>
            <button class="btn btn-danger btn-sm" @click="deleteActivity(activity.id)">Delete</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </app-layout>
</template>

<script>
app.component("user-activity-overview", {
  template: "#user-activity-overview",
  data: () => ({
    activities: []
  }),
  created() {
    this.getAllActivities()
  },
  methods: {
    getAllActivities: function () {
      axios
          .get(`/api/activities`)
          .then((response) => {
            this.activities = response.data;
          })
          .catch((error) => {
            console.error('Error fetching activities:', error);
          });
    },
    fetchActivities: function () {
      const userId = this.$route.params['user-id'];
      axios
          .get(`/api/users/${userId}/activities`)
          .then((response) => {
            this.activities = response.data;
          })
          .catch((error) => {
            console.error('Error fetching activities:', error);
          });
    },
    viewActivity: function (activityId) {
      this.$router.push(`/users/${this.$route.params['user-id']}/activities/${activityId}`);
    },
    deleteActivity: function (activityId) {
      const userId = this.$route.params['user-id'];
      axios
          .delete(`/api/activities/${activityId}`)
          .then(() => {
            this.fetchActivities();
          })
          .catch((error) => {
            console.error('Error deleting activity:', error);
          });
    },
    formatDate(dateTime) {
      // Format the timestamp to a more readable format
      const date = new Date(dateTime);
      return date.toLocaleString();
    }
  }
});
</script>