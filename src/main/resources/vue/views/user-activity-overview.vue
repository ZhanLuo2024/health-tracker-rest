<!--suppress JSAnnotator -->
<template id="user-activity-overview">
  <app-layout>
    <div class="container mt-4">
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
            <!-- Delete Activity -->
            <button
                class="btn btn-danger btn-sm"
                @click="deleteActivity(activity.id, index)"
            >
              Delete
            </button>
          </td>
        </tr>
        </tbody>
      </table>
      <!-- Add Activity Button -->
      <button @click="toggleAddActivity" class="add-button">Add Activity</button>
      <!-- Add Activity Form -->
      <div v-if="showAddForm" class="add-form">
        <div>
          <label>Description</label>
          <input v-model="newActivity.description" type="text" placeholder="Enter description" />
        </div>
        <div>
          <label>Duration (minutes)</label>
          <input v-model="newActivity.duration" type="number" placeholder="Enter duration" />
        </div>
        <div>
          <label>Calories</label>
          <input v-model="newActivity.calories" type="number" placeholder="Enter calories burned" />
        </div>
        <div>
          <label>Started</label>
          <input v-model="newActivity.started" type="datetime-local" />
        </div>
        <button @click="addActivity" class="submit-button">Add</button>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("user-activity-overview", {
  template: "#user-activity-overview",
  data: () => ({
    activities: [],
    userId: "2",
    showAddForm: false,
    newActivity: {
      description: "",
      duration: null,
      calories: null,
      started: "",
    },
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
    deleteActivity(activityId, index) {
      if (!activityId) {
        console.error("Invalid activity ID");
        return;
      }
      axios
          .delete(`/api/activities/${activityId}`)
          .then(() => {
            this.activities.splice(index, 1); // Remove activity from local list
          })
          .catch((error) => {
            console.error("Error deleting activity:", error);
          });
    },
    toggleAddActivity() {
      this.showAddForm = !this.showAddForm;
    },
    addActivity() {
      if (
          this.newActivity.description &&
          this.newActivity.duration &&
          this.newActivity.calories &&
          this.newActivity.started
      ) {
        const uploadActivity = {
          ...this.newActivity,
          userId: this.userId,
        }

        axios
            .post(`/api/activities`, uploadActivity)
            .then(() => {
              this.getAllActivities(); // Reload the list from the server
              this.resetForm();
              this.showAddForm = false;
            })
            .catch((error) => {
              console.error("Error adding activity:", error);
              alert("Failed to add activity. Please try again.");
            });
      } else {
        alert("Please fill in all fields");
      }
    },
    resetForm() {
      this.newActivity = { description: "", duration: null, calories: null, started: "" };
    },
    formatDate(dateTime) {
      // Format the timestamp to a more readable format
      const date = new Date(dateTime);
      return date.toLocaleString();
    }
  }
});
</script>

<style scoped>
.add-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
.add-form {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ddd;
}
.add-form div {
  margin-bottom: 10px;
}
.submit-button {
  margin-top: 10px;
  padding: 5px 15px;
  background-color: #28a745;
  color: white;
  border: none;
  cursor: pointer;
}
.delete-button {
  padding: 5px 10px;
  background-color: #dc3545;
  color: white;
  border: none;
  cursor: pointer;
}
</style>