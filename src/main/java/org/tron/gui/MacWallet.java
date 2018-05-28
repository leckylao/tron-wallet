import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI;
import org.tron.common.utils.Utils;
import org.tron.protos.Protocol;
import org.tron.walletcli.Client;
import org.tron.walletserver.WalletClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class MacWallet
{
    public static String account_address = null;

    private static final Logger logger = LoggerFactory.getLogger("TronWallet");
    private static Client client = new Client();

    public static void main (String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Tron Wallet");
        // create a new GridLayout with two columns
        // of different size
        GridLayout layout = new GridLayout(2, true);
        final Color red = new Color(display,220,53,69);
        final Color black = new Color(display,30,30,30);
        final Color white_grey = new Color(display,238,238,238);
//        final Color white_grey = new Color(display,187,187,187);
        Image logo = new Image(display, "images/tron-logo.png");
        shell.setSize(500, 480);
        shell.setBackground(black);
        shell.setLayout(layout);

        GridData gridData = new GridData(GridData.CENTER, GridData.CENTER, false, false, 2, 1);
        gridData.widthHint = 71;
        gridData.heightHint = 80;
        Canvas canvas = new Canvas(shell, SWT.NONE);
        canvas.setLayoutData(gridData);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent event) {
                if (canvas!= null) {
                    event.gc.drawImage(logo, 0, 0);
                }
            }
        });

        // create new layout data
        GridData data4 = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        GridData text8 = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 2);
        GridData data8 = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);

        MessageBox infoDialog =
                new MessageBox(shell, SWT.ICON_INFORMATION);
        infoDialog.setText("Info");

        MessageBox errorDialog =
                new MessageBox(shell, SWT.ICON_ERROR);
        errorDialog.setText("Error");

        TabFolder folder = new TabFolder(shell, SWT.None);
        folder.setLayoutData(data8);

        //Tab 1
        TabItem tab1 = new TabItem(folder, SWT.None);
        tab1.setText("Account");

        Composite composite = new Composite(folder, SWT.NONE);
        composite.setLayout(layout);

        Label address_label = new Label(composite, SWT.NONE);
        address_label.setForeground(white_grey);
        address_label.setText("Address: ");
        address_label.setLayoutData(data8);

        Label balance_label = new Label(composite, SWT.NONE);
        balance_label.setForeground(white_grey);
        balance_label.setText("Balance: ");
        balance_label.setLayoutData(data8);

        Label bandwidth_label = new Label(composite, SWT.NONE);
        bandwidth_label.setForeground(white_grey);
        bandwidth_label.setText("Bandwidth: ");
        bandwidth_label.setLayoutData(data8);

        Label private_key_label = new Label(composite, SWT.NONE);
        private_key_label.setForeground(white_grey);
        private_key_label.setText("Private Key");
        Text private_key = new Text (composite, SWT.BORDER);
        private_key.setLayoutData(text8);

        Label to_address_label = new Label(composite, SWT.NONE);
        to_address_label.setForeground(white_grey);
        to_address_label.setText("To Address: ");
        Text to_address = new Text (composite, SWT.BORDER);
        to_address.setLayoutData(text8);

        Label to_amount_label = new Label(composite, SWT.NONE);
        to_amount_label.setForeground(white_grey);
        to_amount_label.setText("To Amount (in TRX): ");
        Text to_amount = new Text (composite, SWT.BORDER);
        to_amount.setLayoutData(data4);

        Button login = new Button (composite, SWT.PUSH);
        login.setBackgroundImage(logo);
        login.setText ("Login");

        Button register = new Button (composite, SWT.PUSH);
        register.setText("Register");

        Button sendCoin = new Button (composite, SWT.PUSH);
        sendCoin.setText("Send Coin");
        sendCoin.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String toAddress = to_address.getText();
                String toAmount = to_amount.getText();
                if(toAddress.isEmpty() || toAmount.isEmpty()){
                    logger.info("to address and to amount can't be blank");
                    errorDialog.setMessage("to address and to amount can't be blank");
                    errorDialog.open();
                }else {
                    boolean result = client.sendCoin("1234abcd", toAddress, Long.parseLong(toAmount) * 1000000);
                    if (result) {
                        logger.info("Send " + toAmount + " drop to " + toAddress + " successful !!");
                        infoDialog.setMessage("Send " + toAmount + " drop to " + toAddress + " successful !!");
                        infoDialog.open();
                    } else {
                        logger.info("Send " + toAmount + " drop to " + toAddress + " failed !!");
                        errorDialog.setMessage("Send " + toAmount + " drop to " + toAddress + " failed !!");
                        errorDialog.open();
                    }
                }
            }
        });
        sendCoin.setLayoutData(data4);

        Button backup_address = new Button (composite, SWT.PUSH);
        backup_address.setText("Copy Address");
        backup_address.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (account_address != null) {
                    logger.info("Backup a address successful !!");
                    logger.info("Address = " + account_address);
                    infoDialog.setMessage("Address = " + account_address);
                    infoDialog.open();
                }else{
                    logger.info("No address to copy !!");
                    errorDialog.setMessage("No address to copy !!");
                    errorDialog.open();
                }
            }
        });
        backup_address.setLayoutData(data4);

        Button backup = new Button (composite, SWT.PUSH);
        backup.setText("Copy Private Key");
        backup.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String priKey = client.backupWallet("1234abcd");
                if (priKey != null) {
                    logger.info("Backup a wallet successful !!");
                    logger.info("priKey = " + priKey);
                    infoDialog.setMessage("priKey = " + priKey);
                    infoDialog.open();
                }else{
                    logger.info("No private key to copy !!");
                    errorDialog.setMessage("No private key to copy !!");
                    errorDialog.open();
                }
            }
        });
        backup.setLayoutData(data4);

        Button logout = new Button (composite, SWT.PUSH);
        logout.setText("Logout");
        logout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                client.logout();
                logger.info("Logout successful !!!");
                infoDialog.setMessage("Logout Successful !");
                infoDialog.open();
                address_label.setText("");
                balance_label.setText("");
                account_address = null;
            }
        });
        logout.setLayoutData(data4);

        tab1.setControl(composite);

        // create a new label which is used as a separator
//        Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
//        label.setLayoutData(data8);

        //Tab 2
        TabItem tab2 = new TabItem(folder, SWT.NONE);
        tab2.setText("Vote");

        composite = new Composite(folder, SWT.NONE);
        composite.setLayout(layout);

        Label frozen_balance_label = new Label(composite, SWT.NONE);
        frozen_balance_label.setForeground(white_grey);
        frozen_balance_label.setText("Tron Power: ");
        frozen_balance_label.setLayoutData(data8);

        Label expire_time_label = new Label(composite, SWT.NONE);
        expire_time_label.setForeground(white_grey);
        expire_time_label.setText("Expire Time: ");
        expire_time_label.setLayoutData(data8);

        Label freeze_amount_label = new Label(composite, SWT.NONE);
        freeze_amount_label.setForeground(white_grey);
        freeze_amount_label.setText("Freeze Amount (in TRX): ");
        Text freeze_amount = new Text (composite, SWT.BORDER);
        freeze_amount.setLayoutData(data4);

        login.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(private_key.getText().isEmpty()){
                    logger.info("Private key can't be blank");
                    errorDialog.setMessage("Private key can't be blank!");
                    errorDialog.open();
                }else {
                    if (client.importWallet("1234abcd", private_key.getText())) {
                        logger.info("Import a wallet and store it successful !!");
                        if (client.login("1234abcd")) {
                            logger.info("Login successful !");
                            infoDialog.setMessage("Login Successful !");
                            infoDialog.open();
                            account_address = client.getAddress();
                            logger.info("Address" + account_address);
                            address_label.setText("Address: " + account_address);
                            Protocol.Account account = client.queryAccount();
                            if (account == null) {
                                logger.info("Get Balance failed !!!!");
                            } else {
                                long balance = account.getBalance() / 1000000;
                                logger.info("Balance: " + balance);
                                balance_label.setText("Balance: " + String.valueOf(balance) + "TRX");

                                long bandwidth = account.getBandwidth();
                                logger.info("Bandwidth: " + bandwidth);
                                bandwidth_label.setText("Bandwidth: " + String.valueOf(bandwidth));

                                int frozenCount = account.getFrozenCount();
                                if(frozenCount == 0) {
                                    logger.info("No Tron Power");
                                }else {
                                    long frozenBalance = 0;
                                    long expireTime = 0;
                                    for (Protocol.Account.Frozen frozen : account.getFrozenList()) {
                                        frozenBalance += frozen.getFrozenBalance() / 1000000;
                                        expireTime = frozen.getExpireTime();
                                    }
                                    frozen_balance_label.setText("Tron Power: " + String.valueOf(frozenBalance) + "TRX");
                                    logger.info("frozenBalance: " + frozenBalance);
                                    logger.info("expireTime: " + expireTime);
                                    Date expire_date = new Date(expireTime);
                                    expire_time_label.setText("Expire Time: " + expire_date.toString());
                                }
                            }
                        }
                    } else {
                        logger.info("Import a wallet failed !!");
                        errorDialog.setMessage("Login Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        login.setLayoutData(data4);

        register.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (client.registerWallet("1234abcd")) {
                    logger.info("Register a wallet and store it successful !!");
                    if (client.login("1234abcd")) {
                        infoDialog.setMessage("Register Successful !");
                        infoDialog.open();
                        logger.info("Register successful !");
                        account_address = client.getAddress();
                        logger.info("Address" + account_address);
                        address_label.setText("Address: " + account_address);
                        Protocol.Account account = client.queryAccount();
                        if (account == null) {
                            logger.info("Get Balance failed !!!!");

                        } else {
                            long balance = account.getBalance() / 1000000;
                            logger.info("Balance: " + balance);
                            balance_label.setText("Balance: " + String.valueOf(balance) + "TRX");

                            long bandwidth = account.getBandwidth();
                            logger.info("Bandwidth: " + bandwidth);
                            bandwidth_label.setText("Bandwidth: " + String.valueOf(bandwidth));
                        }
                    }
                } else {
                    logger.info("Register wallet failed !!");
                    errorDialog.setMessage("Register Failed !");
                    errorDialog.open();
                }

            }
        });
        register.setLayoutData(data4);

//        Label freeze_duration_label = new Label(shell, SWT.NONE);
//        freeze_duration_label.setText("Freeze Duration: ");
//        Text freeze_duration = new Text (shell, SWT.BORDER);
//        freeze_duration.setLayoutData(data8);

        Button freeze = new Button (composite, SWT.PUSH);
        freeze.setText ("Freeze");
        freeze.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(freeze_amount.getText().isEmpty()){
                    errorDialog.setMessage("Freeze Amount can't be blank");
                    errorDialog.open();
                }else {
                    long frozen_balance = Long.parseLong(freeze_amount.getText()) * 1000000;
//                    long frozen_duration = 600000 + System.currentTimeMillis();
                    long frozen_duration = 3;
//                    long maxFrozenTime = dbManager.getDynamicPropertiesStore().getMaxFrozenTime();
//                    long minFrozenTime = dbManager.getDynamicPropertiesStore().getMinFrozenTime();

                    boolean result = client.freezeBalance("1234abcd", frozen_balance, frozen_duration);
                    if (result) {
                        logger.info("freezeBalance " + " successful !!");
                        infoDialog.setMessage("Freeze Balance Successful !");
                        infoDialog.open();
                    } else {
                        logger.info("freezeBalance " + " failed !!");
                        errorDialog.setMessage("Freeze Balance Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        freeze.setLayoutData(data4);

        Button unfreeze = new Button (composite, SWT.PUSH);
        unfreeze.setText ("Unfreeze");
        unfreeze.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean result = client.unfreezeBalance("1234abcd");
                if (result) {
                    logger.info("unfreezeBalance " + " successful !!");
                    infoDialog.setMessage("Unfreeze Balance Successful !");
                    infoDialog.open();

                } else {
                    logger.info("unfreezeBalance " + " failed !!");
                    errorDialog.setMessage("Unable to unfreeze TRX. This could be caused because the minimal freeze period hasn't been reached yet.");
                    errorDialog.open();
                }
            }
        });
        unfreeze.setLayoutData(data4);

        // create a new label which is used as a separator
//        label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
//        label.setLayoutData(data8);

        Label vote_address_label = new Label(composite, SWT.NONE);
        vote_address_label.setForeground(white_grey);
        vote_address_label.setText("Witness Address: ");
        Text vote_address = new Text (composite, SWT.BORDER);
        vote_address.setLayoutData(text8);

        Label vote_amount_label = new Label(composite, SWT.NONE);
        vote_amount_label.setForeground(white_grey);
        vote_amount_label.setText("Vote Amount (in TRX): ");
        Text vote_amount = new Text (composite, SWT.BORDER);
        vote_amount.setLayoutData(data4);

        Button vote = new Button (composite, SWT.PUSH);
        vote.setText ("Vote Witness");
        vote.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(vote_address.getText().isEmpty() || vote_amount.getText().isEmpty()){
                    errorDialog.setMessage("Witness address and vote amount can't be blank");
                    errorDialog.open();
                }else {
                    HashMap<String, String> witness = new HashMap<String, String>();
                    witness.put(vote_address.getText(), vote_amount.getText());

                    boolean result = client.voteWitness("1234abcd", witness);
                    if (result) {
                        logger.info("VoteWitness " + " successful !!");
                        infoDialog.setMessage("Vote Witness Successful !");
                        infoDialog.open();
                    } else {
                        logger.info("VoteWitness " + " failed !!");
                        errorDialog.setMessage("Vote Witness Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        vote.setLayoutData(data4);

        tab2.setControl(composite);

        //Tab 3
        TabItem tab3 = new TabItem(folder, SWT.None);
        tab3.setText("Witnesses");

        ScrolledComposite scroller = new ScrolledComposite(folder, SWT.V_SCROLL);
        scroller.setLayout(layout);

        composite = new Composite(scroller, SWT.NONE);
        composite.setLayout(layout);

        Optional<GrpcAPI.WitnessList> result = client.listWitnesses();
        if (result.isPresent()) {
            GrpcAPI.WitnessList witnessList = result.get();
            for (Protocol.Witness witness : witnessList.getWitnessesList()) {
                Label witness_address = new Label(scroller, SWT.NONE);
                witness_address.setForeground(white_grey);
                witness_address.setText("Address: " + WalletClient.encode58Check(witness.getAddress().toByteArray()));

                Label witness_vote = new Label(scroller, SWT.NONE);
                witness_vote.setForeground(white_grey);
                witness_vote.setText("Votes: " + witness.getVoteCount());

                Label witness_url = new Label(scroller, SWT.NONE);
                witness_url.setForeground(white_grey);
                witness_url.setText("URL: " + witness.getUrl());

                Label witness_produced = new Label(scroller, SWT.NONE);
                witness_produced.setForeground(white_grey);
                witness_produced.setText("Total Produced: " + witness.getTotalProduced());

                Label witness_missed = new Label(scroller, SWT.NONE);
                witness_missed.setForeground(white_grey);
                witness_missed.setText("Total Missed: " + witness.getTotalMissed());

                Label witness_block = new Label(scroller, SWT.NONE);
                witness_block.setForeground(white_grey);
                witness_block.setText("Latest Block: " + witness.getLatestBlockNum());

                Label witness_active = new Label(scroller, SWT.NONE);
                witness_active.setForeground(white_grey);
                witness_active.setText("Active: " + witness.getIsJobs());

                // create a new label which is used as a separator
                Label label = new Label(scroller, SWT.SEPARATOR | SWT.HORIZONTAL);
                label.setLayoutData(data8);
            }
        } else {
            logger.info("List witnesses " + " failed !!");
            errorDialog.setMessage("List witnesses " + " failed !!");
            errorDialog.open();
        }

        scroller.setContent(composite);

        // create some controls in TabArea and assign a layout to TabArea
        scroller.setExpandVertical(true);
        scroller.setExpandHorizontal(true);
        scroller.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        tab3.setControl(scroller);

        //Tab 4
        TabItem tab4 = new TabItem(folder, SWT.NONE);
        tab4.setText("History");

        composite = new Composite(folder, SWT.NONE);
        composite.setLayout(layout);

        tab4.setControl(composite);

//        shell.setDefaultButton (vote);
//        shell.setLayout (new RowLayout ());
//        private_key_label.pack();
//        vote_address_label.pack();
//        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
        logo.dispose();
        display.dispose();
    }

}
