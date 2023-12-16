package com.projetihm;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class App extends Application {

    private ObservableList<Task> tasks;
    private ObservableList<Task> highPriorityTasks;
    private ObservableList<Task> completedTasks;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List");

        // Initialize the task lists
        tasks = FXCollections.observableArrayList();
        highPriorityTasks = FXCollections.observableArrayList();
        completedTasks = FXCollections.observableArrayList();

        // UI Components
        ListView<Task> taskListView = createListView(tasks);
        ListView<Task> highPriorityListView = createListView(highPriorityTasks);
        ListView<Task> completedListView = createListView(completedTasks);

        Button addButton = new Button("Add Task");
        Button editButton = new Button("Edit Task");
        Button removeButton = new Button("Remove Task");
        Button moveButton = new Button("Move Task");

        // Add Task Button Event
        addButton.setOnAction(event -> {
            Task newTask = showAddTaskDialog(null);
            if (newTask != null) {
                tasks.add(newTask);
            }
        });

        // Edit Task Button Event
        editButton.setOnAction(event -> {
            Task selectedTask = getSelectedTask(taskListView);
            if (selectedTask != null) {
                Task updatedTask = showAddTaskDialog(selectedTask);
                if (updatedTask != null) {
                    tasks.remove(selectedTask);
                    tasks.add(updatedTask);
                }
            }
        });

        // Remove Task Button Event
        removeButton.setOnAction(event -> {
            Task selectedTask = getSelectedTask(taskListView);
            if (selectedTask != null) {
                tasks.remove(selectedTask);
            }
        });

        // Move Task Button Event
        moveButton.setOnAction(event -> {
            Task selectedTask = getSelectedTask(taskListView);
            if (selectedTask != null) {
                showMoveTaskDialog(selectedTask);
            }
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        HBox listViewContainer = new HBox(10);
        listViewContainer.getChildren().addAll(taskListView, highPriorityListView, completedListView);

        layout.getChildren().addAll(listViewContainer, addButton, editButton, removeButton, moveButton);

        // Scene
        Scene scene = new Scene(layout, 900, 300);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    private ListView<Task> createListView(ObservableList<Task> taskList) {
        ListView<Task> listView = new ListView<>(taskList);

        // Enable selection of multiple items
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        return listView;
    }

    private Task showAddTaskDialog(Task existingTask) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle(existingTask == null ? "Add Task" : "Edit Task");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType editButton = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(existingTask == null ? addButton : editButton, ButtonType.CANCEL);

        // Create task details form
        VBox formLayout = new VBox(10);
        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        DatePicker datePicker = new DatePicker();

        if (existingTask != null) {
            titleField.setText(existingTask.getTitle());
            descriptionField.setText(existingTask.getDescription());

            // Convert the string representation of the date to a LocalDate object
            if (existingTask.getDueDate() != null) {
                datePicker.setValue(existingTask.getDueDate());
            }
        }

        formLayout.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionField,
                new Label("Due Date:"), datePicker
        );

        dialog.getDialogPane().setContent(formLayout);

        // Convert the result to a task when the addButton/editButton is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == (existingTask == null ? addButton : editButton)) {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                LocalDate dueDate = datePicker.getValue();

                if (!title.isEmpty() && !description.isEmpty()) {
                    return new Task(title, description, dueDate);
                }
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        return dialog.showAndWait().orElse(null);
    }

    private Task getSelectedTask(ListView<Task> listView) {
        if (!listView.getSelectionModel().isEmpty()) {
            return listView.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    private void showMoveTaskDialog(Task task) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Move Task");

        // Set the button types
        ButtonType moveButton = new ButtonType("Move", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(moveButton, ButtonType.CANCEL);

        // Create move task form
        VBox formLayout = new VBox(10);
        ChoiceBox<String> destinationListChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "To-Do List", "High Priority", "Completed"
        ));

        formLayout.getChildren().addAll(
                new Label("Select Destination List:"), destinationListChoiceBox
        );

        dialog.getDialogPane().setContent(formLayout);

        // Convert the result to the destination list when the moveButton is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == moveButton) {
                return destinationListChoiceBox.getValue();
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        String destinationList = dialog.showAndWait().orElse(null);

        // Move the task to the selected destination list
        if (destinationList != null && !destinationList.equals(task.getListName())) {
            tasks.remove(task);
            task.setListName(destinationList);
            tasks.add(task);
        }
    }

    public static class Task {
        private String title;
        private String description;
        private String listName;
        private LocalDate dueDate;

        public Task(String title, String description, LocalDate dueDate) {
            this.title = title;
            this.description = description;
            this.listName = "To-Do List";
            this.dueDate = dueDate;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getListName() {
            return listName;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}