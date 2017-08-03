package gitlet.core;

public class Branch {
    private String name;
    private Commit branchHead;

    public Branch(String name, Commit branchHead) {
        this.name = name;
        this.branchHead = branchHead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getHeadCommit() {
        return branchHead;
    }

    public void setHeadCommit(Commit newBranchHead) {
        this.branchHead = newBranchHead;
    }
}
