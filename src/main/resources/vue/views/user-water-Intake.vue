<!--suppress JSAnnotator -->
<template id="water-intake-overview">
  <app-layout>
    <div>
      <!-- Page Title -->
      <h2>Water Intake Overview</h2>

      <!-- Total Water Intake -->
      <div class="summary">
        <p><strong>Total Water Intake:</strong> {{ totalWaterIntake }}</p>
        <p><strong>Number of Records:</strong> {{ waterIntakes.length }}</p>
      </div>

      <!-- Chart -->
      <div class="chart-section">
        <button class="btn btn-primary mb-3" @click="toggleChart">
          {{ showChart ? 'Hide Water Intake Chart' : 'Show Water Intake Chart' }}
        </button>
        <div v-show="showChart" class="chart-container">
          <canvas id="waterIntakeChart"></canvas>
        </div>
      </div>

      <!-- Add spacing between chart and table -->
      <div style="margin-top: 20px;"></div>

      <!-- Water Intake Records Table -->
      <table class="table table-striped table-hover table-bordered text-center">
      <thead>
        <tr>
          <th style="text-align: left;">#</th>
          <th style="text-align: right;">Amount (L)</th>
          <th style="text-align: center;">Recorded At</th>
          <th style="text-align: center;">Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(intake, index) in waterIntakes" :key="intake.id">
          <td style="text-align: left;">{{ index + 1 }}</td>
          <td style="text-align: right;">{{ intake.amount }}</td>
          <td style="text-align: center;">{{ formatDate(intake.recordedAt) }}</td>
          <td style="text-align: center;">
            <button @click="deleteWaterIntake(intake.id, index)" class="delete-button">Delete</button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Add Water Intake Form Toggle -->
      <button @click="toggleAddForm" class="add-button">Add Water Intake</button>

      <!-- Add Water Intake Form -->
      <div v-if="showAddForm" class="add-form">
        <div class="form-group">
          <label>Amount (L)
            <input
                v-model.number="newWaterIntake.amount" type="number" placeholder="Enter amount"
            />
          </label>
        </div>
        <div class="form-group">
          <label>Recorded At
            <input
                v-model="newWaterIntake.recordedAt" type="datetime-local"
            />
          </label>
        </div>
        <div class="form-group">
          <button @click="addWaterIntake" class="save-button">Add</button>
        </div>
      </div>

    </div>
  </app-layout>
</template>

<script>
app.component("water-intake-overview", {
  template: "#water-intake-overview",
  data: () => ({
    waterIntakes: [], // List of water intake records
    totalWaterIntake: 0,
    showAddForm: false, // Toggle add form visibility
    showChart: false,
    chartInstance: null, // init chart instance
    newWaterIntake: {
      amount: 0,
      recordedAt: "",
    },
  }),
  created() {
    this.fetchWaterIntakeRecords();
  },
  methods: {
    fetchWaterIntakeRecords() {
      console.log(window["vue3-smooth-progress"]);
      // Fetch water intake records for user with ID 2
      axios
          .get("/api/waterIntakes/user/2")
          .then((response) => {
            this.waterIntakes = response.data.waterIntakes;
            this.totalWaterIntake = response.data.totalWaterIntake;
          })
          .catch((error) => {
            console.error("Error fetching water intake records:", error);
            alert("Failed to load water intake records.");
          });
    },
    deleteWaterIntake(id, index) {
      // Delete a specific water intake record
      axios
          .delete(`/api/activities/${id}`)
          .then(() => {
            this.waterIntakes.splice(index, 1); // Remove record from the list
            this.calculateTotalWaterIntake();
          })
          .catch((error) => {
            console.error("Error deleting water intake record:", error);
            alert("Failed to delete record.");
          });
    },
    addWaterIntake() {
      // Add a new water intake record
      if (!this.newWaterIntake.amount || !this.newWaterIntake.recordedAt) {
        alert("Please fill in all fields.");
        return;
      }
      axios
          .post("/api/waterintakes", {
            "user-id": 2,
            amount: Number(this.newWaterIntake.amount).toFixed(2),
            recordedAtString: this.newWaterIntake.recordedAt,
          })
          .then(() => {
            this.fetchWaterIntakeRecords();
            this.resetForm();
            this.showAddForm = false;
            // chart data reload
            if (this.showChart) {
              this.renderChart(); // reload when add new data
            }
          })
          .catch((error) => {
            console.error("Error adding water intake record:", error.response?.data || error.message);
            alert("Failed to add record.");
          });
    },
    resetForm() {
      this.newWaterIntake = { description: "", amount: null, recordedAt: "" };
    },
    toggleAddForm() {
      this.showAddForm = !this.showAddForm;
    },
    calculateTotalWaterIntake() {
      // Recalculate total water intake
      this.totalWaterIntake = this.waterIntakes.reduce((sum, intake) => sum + intake.amount, 0);
    },
    toggleChart() {
      this.showChart = !this.showChart;
      if (this.showChart) {
        this.renderChart();
      }
    },
    renderChart() {
      // get canvas content
      const ctx = document.getElementById('waterIntakeChart').getContext('2d');

      // Memory leak prevention
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      // create line chart
      this.chartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.waterIntakes.map(intake => this.formatDate(intake.recordedAt)),
          datasets: [
            {
              label: 'Water Intake (L)',
              data: this.waterIntakes.map(intake => intake.amount),
              borderColor: 'rgba(87, 160, 229, 1)',
              backgroundColor: 'rgba(87, 160, 229, 0.2)',
              fill: true,
            },
          ],
        },
        options: {
          maintainAspectRatio: false,
        },
      });
    },
    formatDate(dateTime) {
      // Format the date to a human-readable format
      const date = new Date(dateTime);
      return date.toLocaleString();
    },
    beforeDestroy() {
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }
    },
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

.chart-container {
  width: 100%;
  height: 400px;
}
</style>