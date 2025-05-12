import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ToDoWidgetGui {
    public static void main(String[] args) {
        // Create the main window (JFrame)
        JFrame frame = new JFrame("To Do List App");
        frame.setResizable(true);
        frame.setUndecorated(true);
        frame.setOpacity(0.9f);
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set up message icon
        ImageIcon originalIcon = new ImageIcon("src/cat.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(scaledImage);

        // Allow for frame to be dragged
        final Point clickPoint = new Point();
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clickPoint.x = e.getX();
                clickPoint.y = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point newPoint = frame.getLocation();
                newPoint.translate(e.getX() - clickPoint.x, e.getY() - clickPoint.y);
                frame.setLocation(newPoint);
            }
        });

        // Create input panel (top)
        JPanel inputPanel = new JPanel();
        JTextField todo = new JTextField(20);
        JButton fetchButton = new JButton("Add Task");
        inputPanel.add(new JLabel("Enter task to do:"));
        inputPanel.add(todo);
        inputPanel.add(fetchButton);

        final int[] score = {0};
        JLabel scoreLabel = new JLabel("Tasks Left Until Reward: " + score[0]);
        Border border = BorderFactory.createLineBorder(new Color(47, 75, 51), 1, true);
        Border margin = new EmptyBorder(10,100,10,100);
        scoreLabel.setBorder(new CompoundBorder(border, margin));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crate report panel (bottom)
        JPanel reportPanel = new JPanel();
        reportPanel.setBackground(new Color(47, 75, 51));

        // Create output panel (center)
        JPanel outputArea = new JPanel();
        outputArea.setLayout(new BoxLayout(outputArea, BoxLayout.Y_AXIS));
        outputArea.setOpaque(false);
        outputArea.add(scoreLabel);

        // Add panels to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(outputArea, BorderLayout.CENTER);
        frame.add(reportPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);

        // Create list storage file
        try {
            File myObj = new File("todos.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Read list from storage file
        try {
            File myObj = new File("todos.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] listItem = data.split(",");

                JCheckBox item = new JCheckBox(listItem[0] + "\n", listItem[1].equals("true"));
                outputArea.add(item);
                if (!item.isSelected()) {
                    score[0]++;
                }
                scoreLabel.setText("Tasks Left Until Reward: " + score[0]);
                item.setAlignmentX(Component.CENTER_ALIGNMENT);
                item.addActionListener(ev -> {

                    if (!item.isSelected()) {
                        score[0]++;
                        scoreLabel.setText("Tasks Left Until Reward: " + score[0]);

                        // Update file
                        try {
                            File inputFile = new File("todos.txt");
                            Scanner scanner = new Scanner(inputFile);
                            StringBuilder updatedContent = new StringBuilder();

                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                System.out.println(line);
                                String[] parts = line.split(",");
                                if (parts.length == 2 && parts[0].equals(item.getText().trim())) {
                                    updatedContent.append(parts[0]).append(",false\n");
                                } else {
                                    updatedContent.append(line).append("\n");
                                }
                            }
                            scanner.close();

                            FileWriter writer = new FileWriter("todos.txt", false); // overwrite file
                            writer.write(updatedContent.toString());
                            writer.close();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                            if (item.isSelected()) {
                                JOptionPane.showMessageDialog(frame, "Congrats! You completed a To Do: " + item.getText(), "TO DO COMPLETED", JOptionPane.INFORMATION_MESSAGE, icon);
                                score[0]--;
                                scoreLabel.setText("Tasks Left Until Reward: " + score[0]);

                                // Update file
                                try {
                                    File inputFile = new File("todos.txt");
                                    Scanner scanner = new Scanner(inputFile);
                                    StringBuilder updatedContent = new StringBuilder();

                                    while (scanner.hasNextLine()) {
                                        String line = scanner.nextLine();
                                        String[] parts = line.split(",");
                                        if (parts.length == 2 && parts[0].equals(item.getText().trim())) {
                                            updatedContent.append(parts[0]).append(",true\n");
                                            System.out.println(parts[0]);
                                        } else {
                                            updatedContent.append(line).append("\n");
                                        }
                                    }
                                    scanner.close();

                                    FileWriter writer = new FileWriter("todos.txt", false); // overwrite file
                                    writer.write(updatedContent.toString());
                                    writer.close();

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                });
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Create report panel
        try{
            Weather weather = WeatherService.getWeather("Miami");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String currentReport = "☼ Location: Miami ☼ " + weather.toString() + " ☼ Date: " + now.format(formatter) + " ☼";
            JLabel reportLabel = new JLabel(currentReport);
            reportLabel.setForeground(Color.WHITE);
            reportLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            reportPanel.add(reportLabel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
        }


        // Handle task creation
        fetchButton.addActionListener(e -> {
            String ToDo = todo.getText().trim();
            if (ToDo.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a to do.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create to do list item and add to frame
            JCheckBox item = new JCheckBox(ToDo + "\n");
            outputArea.add(item);

            score[0]++;
            scoreLabel.setText("Tasks Left: " + score[0]);
            item.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add item to storage file
            try {
                FileWriter myWriter = new FileWriter("todos.txt", true);
                myWriter.write(ToDo + "," + "false" + "\n");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException error) {
                System.out.println("An error occurred.");
                error.printStackTrace();
            }

            // Refresh the panel to show the new checkbox
            outputArea.revalidate();
            outputArea.repaint();
            todo.setText("");

            // Add completion action listener for to do items
            item.addActionListener(ev -> {
                if (item.isSelected()) {
                    JOptionPane.showMessageDialog(frame, "Congrats! You completed a To Do: " + item.getText(), "TO DO COMPLETED", JOptionPane.INFORMATION_MESSAGE, icon);
                    score[0]--;
                    scoreLabel.setText("Tasks Left Until Reward: " + score[0]);

                    // Update file
                    try {
                        File inputFile = new File("todos.txt");
                        Scanner scanner = new Scanner(inputFile);
                        StringBuilder updatedContent = new StringBuilder();

                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            System.out.println(line);
                            String[] parts = line.split(",");
                            if (parts.length == 2 && parts[0].equals(item.getText().trim())) {
                                updatedContent.append(parts[0]).append(",true\n");
                            } else {
                                updatedContent.append(line).append("\n");
                            }
                        }
                        scanner.close();

                        FileWriter writer = new FileWriter("todos.txt", false); // overwrite file
                        writer.write(updatedContent.toString());
                        writer.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                if (!item.isSelected()) {
                    score[0]++;
                    scoreLabel.setText("Tasks Left Until Reward: " + score[0]);

                    // Update file
                    try {
                        File inputFile = new File("todos.txt");
                        Scanner scanner = new Scanner(inputFile);
                        StringBuilder updatedContent = new StringBuilder();

                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            System.out.println(line);
                            String[] parts = line.split(",");
                            if (parts.length == 2 && parts[0].equals(item.getText().trim())) {
                                updatedContent.append(parts[0]).append(",false\n");
                            } else {
                                updatedContent.append(line).append("\n");
                            }
                        }
                        scanner.close();

                        FileWriter writer = new FileWriter("todos.txt", false); // overwrite file
                        writer.write(updatedContent.toString());
                        writer.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        });

        // Make the window visible
        frame.setVisible(true);
    }
}
