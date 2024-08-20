import java.security.*;
import java.util.ArrayList;

class Transaction {
    public String transactionId;
    public String sender;
    public String receiver;
    public String drugDetails;
    private long timestamp;

    public Transaction(String sender, String receiver, String drugDetails) {
        this.sender = sender;
        this.receiver = receiver;
        this.drugDetails = drugDetails;
        this.timestamp = System.currentTimeMillis();
        this.transactionId = generateTransactionId();
    }

    private String generateTransactionId() {
        return applySha256(sender + receiver + drugDetails + timestamp);
    }

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder(); 
            for (byte elem : hash) {
                String hex = Integer.toHexString(0xff & elem);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId + "\nSender: " + sender + "\nReceiver: " + receiver + "\nDrug Details: " + drugDetails + "\nTimestamp: " + timestamp;
    }
}

class Block {
    public String hash;
    public String previousHash;
    private ArrayList<Transaction> transactions;
    private long timestamp;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        this.hash = calculateHash();
    }

    public String calculateHash() {  // Changed from private to public
        String data = previousHash + timestamp + transactions.toString();
        return Transaction.applySha256(data);
    }

    @Override
    public String toString() {
        return "Block Hash: " + hash + "\nPrevious Hash: " + previousHash + "\nTransactions: " + transactions + "\nTimestamp: " + timestamp;
    }
}

class Blockchain {
    private ArrayList<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        // Add genesis block
        this.chain.add(new Block("0"));
    }

    public void addBlock(Block block) {
        block.previousHash = this.chain.get(this.chain.size() - 1).hash;
        block.hash = block.calculateHash();
        this.chain.add(block);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }
            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder blockchainData = new StringBuilder();
        for (Block block : chain) {
            blockchainData.append(block.toString()).append("\n");
        }
        return blockchainData.toString();
    }
}

public class PharmaSupplyChain {
    public static void main(String[] args) {
        Blockchain pharmaBlockchain = new Blockchain();

        Block block1 = new Block(pharmaBlockchain.toString());
        block1.addTransaction(new Transaction("Manufacturer", "Distributor", "Drug A - Batch 123"));
        pharmaBlockchain.addBlock(block1);

        Block block2 = new Block(pharmaBlockchain.toString());
        block2.addTransaction(new Transaction("Distributor", "Pharmacy", "Drug A - Batch 123"));
        pharmaBlockchain.addBlock(block2);

        System.out.println("Blockchain is valid: " + pharmaBlockchain.isChainValid());
        System.out.println(pharmaBlockchain.toString());
    }
}

