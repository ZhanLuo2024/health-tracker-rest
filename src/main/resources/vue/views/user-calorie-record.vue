<!--suppress JSAnnotator -->
<template id="user-calorie-record">
  <app-layout>
    <div>
      <!-- Page Title -->
      <h2>Calorie Log Overview</h2>

      <!-- Total Calorie Summary -->
      <div class="summary">
        <p><strong>Total Calories:</strong> {{ totalCalories }}</p>
        <p><strong>Number of Records:</strong> {{ calorieLogs.length }}</p>
      </div>

      <!-- Calorie Log Records Table -->
      <table>
        <thead>
        <tr>
          <th style="text-align: left;">#</th>
          <th style="text-align: right;">Calories</th>
          <th style="text-align: center;">Recorded At</th>
          <th style="text-align: center;">Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(log, index) in calorieLogs" :key="log.id">
          <td style="text-align: left;">{{ index + 1 }}</td>
          <td style="text-align: right;">{{ log.calories }}</td>
          <td style="text-align: center;">{{ formatDate(log.recordedAt) }}</td>
          <td style="text-align: center;">
            <button @click="deleteCalorieLog(intake.id, index)" class="delete-button">Delete</button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Add Water Intake Form Toggle -->
      <button @click="toggleAddForm" class="add-button">Add Calorie Log</button>

      <!-- Add Water Intake Form -->
      <div v-if="showAddForm" class="add-form">
        <div class="form-group">
          <label>Amount (L)
            <input
                v-model.number="newCalorieLog.calories" type="number" placeholder="Enter calories"
            />
          </label>
        </div>
        <div class="form-group">
          <label>Recorded At
            <input
                v-model="newCalorieLog.recordedAt" type="datetime-local"
            />
          </label>
        </div>
        <div class="form-group">
          <button @click="addCalorieLog" class="save-button">Add</button>
        </div>
      </div>

    </div>
  </app-layout>
</template>

<script>
app.component("user-calorie-record", {
  template: "#user-calorie-record",
  data: () => ({
    totalCalories: 0,
    calorieLogs: [],
    showAddForm: false, // Toggle add form visibility
    newWaterIntake: {
      calories: 0,
      recordedAt: "",
    },
  }),
  created() {
    axios
        .get("/api/calories/user/2")
        .then((response) => {
          this.totalCalories = response.data.totalCalories;
          this.calorieLogs = response.data.calorieLogs;
        })
        .catch(() => {
          alert("Error while fetching calorie logs");
        });
  },
  methods: {
    deleteCalorieLog(id, index) {
      // Delete a specific calorie log record
      axios
          .delete(`/api/calorielogs/${id}`)
          .then(() => {
            this.calorieLogs.splice(index, 1); // Remove record from the list
            this.totalCalories = this.calorieLogs.reduce((sum, log) => sum + log.calories, 0); // Recalculate total
          })
          .catch((error) => {
            console.error("Error deleting calorie log record:", error);
            alert("Failed to delete record.");
          });
    },
    formatDate(dateTime) {
      // Format the date to a human-readable format
      const date = new Date(dateTime);
      return date.toLocaleString();
    }
  },
});
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
.summary {
  margin-bottom: 20px;
}
.summary p {
  margin: 5px 0;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}
th {
  background-color: #f4f4f4;
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