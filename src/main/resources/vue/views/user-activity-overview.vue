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
        <div class="form-group">
          <label for="description">Description</label>
          <input
              id="description"
              v-model="newActivity.description"
              placeholder="Enter description"
          />
        </div>
        <div class="form-group">
          <label for="duration">Duration (minutes)</label>
          <input
              id="duration"
              v-model.number="newActivity.duration"
              type="number"
              placeholder="Enter duration"
          />
        </div>
        <div class="form-group">
          <label for="calories">Calories</label>
          <input
              id="calories"
              v-model.number="newActivity.calories"
              type="number"
              placeholder="Enter calories"
          />
        </div>
        <div class="form-group">
          <label for="started">Started</label>
          <input id="started" v-model="newActivity.started" type="datetime-local" />
        </div>
        <button @click="addActivity" class="save-button">Add</button>
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
h2 {
  margin-bottom: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ddd;
  padding: 8px;
}
th {
  background-color: #f4f4f4;
  text-align: center;
}
td {
  text-align: center;
}
tr:nth-child(even) {
  background-color: #f9f9f9;
}
.delete-button {
  padding: 5px 10px;
  background-color: #dc3545;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}
.delete-button:hover {
  background-color: #c82333;
}

.add-button {
  padding: 10px 15px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  margin: 20px 0;
}
.add-button:hover {
  background-color: #0056b3;
}

.add-form {
  display: flex;
  flex-direction: column;
  gap: 15px; /* 每个表单组之间的间距 */
  margin-top: 10px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 100%;
}

.save-button {
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 10px;
}

.save-button:hover {
  background-color: #218838;
}

</style>