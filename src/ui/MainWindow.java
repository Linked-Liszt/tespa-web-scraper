package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JTextField;
import javax.swing.SwingWorker;

import java.util.List;

import scraper.TeamScraper;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;

public class MainWindow {

	private JFrame frmScrapeboisWeOut;
	private JTextField startingIndex;
	private JTextField endingIndex;
	private JLabel lblUrl;
	private JButton btnScrape;
	private JScrollPane scrollPane;
	private JTextArea ResultsArea;
	private JTextField scrapingUrl;


	private String BaseUrl = "https://compete.tespa.org/tournament/97/match/";
	private JLabel lblTargetTeam;
	private JTextField Target;
	private JProgressBar progressBar;

	private SwingWorker<String, ArrayList<String>> AsyncWorker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmScrapeboisWeOut.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}


	private void AttemptScrape() {
		if (btnScrape.getText() == "Stop") {
			btnScrape.setText("Start");
			try {
				AsyncWorker.cancel(true);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		else {
			ResultsArea.setText("");
			btnScrape.setText("Stop");

			//Begin Async Task
			AsyncWorker = new SwingWorker<String, ArrayList<String>>() {
				@Override
				protected String doInBackground() throws Exception {
					//Data Validation
					int SIndex = 0;
					int EIndex = 0;
					String TargetString = Target.getText();
					String Url = scrapingUrl.getText();

					String outputString = "";

					try {
						SIndex = Integer.parseInt(startingIndex.getText());
						EIndex= Integer.parseInt(endingIndex.getText());
					}
					catch(NumberFormatException e) {
						JOptionPane.showMessageDialog(frmScrapeboisWeOut, "Please enter Int's into Indexes");
						return "";
					}

					if (SIndex > EIndex) {
						JOptionPane.showMessageDialog(frmScrapeboisWeOut, "Starting Index Must Be Lower than Ending");
						return "";
					}

					// Simulate doing something useful.
					for (int i = SIndex; i <= EIndex; i++) {
						if (isCancelled()) break;
						
						TeamScraper scrape = new TeamScraper(Url + i);
						if(scrape.hasTeam(TargetString)) {
							outputString = outputString + scrape.createOutput() + "\n\n";
						}

						ArrayList<String> PublishList = new ArrayList<String>();
						PublishList.add(Integer.toString((((i-SIndex)*100))/(EIndex-SIndex)));
						PublishList.add(outputString);
						publish(PublishList);
					}

					return outputString;
				}

				// Can safely update the GUI from this method.
				@Override
				protected void done() {

					String status;
					btnScrape.setText("Start");
					try {
						status = get();
						btnScrape.setText("Start");
						progressBar.setValue(100);
						ResultsArea.setText(status);
						ResultsArea.revalidate();
					} catch (InterruptedException e) {
						ResultsArea.setText(ResultsArea.getText() + "\nStopped Here");
					} catch (ExecutionException e) {
						e.printStackTrace();
						ResultsArea.setText(ResultsArea.getText() + "\nBad URL Occurred Here");
						// This is thrown if we throw an exception
						// from doInBackground.
					}
				}

				@Override
				// Can safely update the GUI from this method.
				protected void process(List<ArrayList<String>> chunks) {
					// Here we receive the values that we publish().
					// They may come grouped in chunks.

					ArrayList<String> mostRecentValue = chunks.get(chunks.size()-1);
					System.out.println(mostRecentValue.get(0));
					progressBar.setValue(Integer.parseInt(mostRecentValue.get(0)));
					ResultsArea.setText(mostRecentValue.get(1));
					ResultsArea.revalidate();
				}	

			};

			AsyncWorker.execute();

		}



	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScrapeboisWeOut = new JFrame();
		frmScrapeboisWeOut.setTitle("ScrapeBois We Out Here");
		frmScrapeboisWeOut.setBounds(500, 500, 715, 515);
		frmScrapeboisWeOut.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		frmScrapeboisWeOut.getContentPane().setLayout(gridBagLayout);

		lblUrl = new JLabel("URL:");
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.anchor = GridBagConstraints.EAST;
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 0;
		gbc_lblUrl.gridy = 0;
		frmScrapeboisWeOut.getContentPane().add(lblUrl, gbc_lblUrl);

		scrapingUrl = new JTextField();
		scrapingUrl.setText(BaseUrl);
		GridBagConstraints gbc_scrapingUrl = new GridBagConstraints();
		gbc_scrapingUrl.insets = new Insets(0, 0, 5, 0);
		gbc_scrapingUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrapingUrl.gridx = 1;
		gbc_scrapingUrl.gridy = 0;
		frmScrapeboisWeOut.getContentPane().add(scrapingUrl, gbc_scrapingUrl);
		scrapingUrl.setColumns(10);

		JLabel lblStartingIndex = new JLabel("Starting Index: ");
		GridBagConstraints gbc_lblStartingIndex = new GridBagConstraints();
		gbc_lblStartingIndex.anchor = GridBagConstraints.EAST;
		gbc_lblStartingIndex.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingIndex.gridx = 0;
		gbc_lblStartingIndex.gridy = 1;
		frmScrapeboisWeOut.getContentPane().add(lblStartingIndex, gbc_lblStartingIndex);

		startingIndex = new JTextField();
		GridBagConstraints gbc_startingIndex = new GridBagConstraints();
		gbc_startingIndex.insets = new Insets(0, 0, 5, 0);
		gbc_startingIndex.fill = GridBagConstraints.HORIZONTAL;
		gbc_startingIndex.gridx = 1;
		gbc_startingIndex.gridy = 1;
		frmScrapeboisWeOut.getContentPane().add(startingIndex, gbc_startingIndex);
		startingIndex.setColumns(10);

		JLabel lblEndingIndex = new JLabel("Ending Index: ");
		GridBagConstraints gbc_lblEndingIndex = new GridBagConstraints();
		gbc_lblEndingIndex.anchor = GridBagConstraints.EAST;
		gbc_lblEndingIndex.insets = new Insets(0, 0, 5, 5);
		gbc_lblEndingIndex.gridx = 0;
		gbc_lblEndingIndex.gridy = 2;
		frmScrapeboisWeOut.getContentPane().add(lblEndingIndex, gbc_lblEndingIndex);

		endingIndex = new JTextField();
		GridBagConstraints gbc_endingIndex = new GridBagConstraints();
		gbc_endingIndex.insets = new Insets(0, 0, 5, 0);
		gbc_endingIndex.fill = GridBagConstraints.HORIZONTAL;
		gbc_endingIndex.gridx = 1;
		gbc_endingIndex.gridy = 2;
		frmScrapeboisWeOut.getContentPane().add(endingIndex, gbc_endingIndex);
		endingIndex.setColumns(10);

		lblTargetTeam = new JLabel("Target Team");
		GridBagConstraints gbc_lblTargetTeam = new GridBagConstraints();
		gbc_lblTargetTeam.anchor = GridBagConstraints.EAST;
		gbc_lblTargetTeam.insets = new Insets(0, 0, 5, 5);
		gbc_lblTargetTeam.gridx = 0;
		gbc_lblTargetTeam.gridy = 3;
		frmScrapeboisWeOut.getContentPane().add(lblTargetTeam, gbc_lblTargetTeam);

		Target = new JTextField();
		GridBagConstraints gbc_Target = new GridBagConstraints();
		gbc_Target.insets = new Insets(0, 0, 5, 0);
		gbc_Target.fill = GridBagConstraints.HORIZONTAL;
		gbc_Target.gridx = 1;
		gbc_Target.gridy = 3;
		frmScrapeboisWeOut.getContentPane().add(Target, gbc_Target);
		Target.setColumns(10);

		btnScrape = new JButton("Scrape");
		btnScrape.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AttemptScrape();
			}
		});
		GridBagConstraints gbc_btnScrape = new GridBagConstraints();
		gbc_btnScrape.insets = new Insets(0, 0, 5, 5);
		gbc_btnScrape.gridx = 0;
		gbc_btnScrape.gridy = 4;
		frmScrapeboisWeOut.getContentPane().add(btnScrape, gbc_btnScrape);

		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 4;
		frmScrapeboisWeOut.getContentPane().add(progressBar, gbc_progressBar);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		frmScrapeboisWeOut.getContentPane().add(scrollPane, gbc_scrollPane);

		ResultsArea = new JTextArea();
		ResultsArea.setEditable(false);
		ResultsArea.setText("Results Here");
		scrollPane.setViewportView(ResultsArea);
	}

}
