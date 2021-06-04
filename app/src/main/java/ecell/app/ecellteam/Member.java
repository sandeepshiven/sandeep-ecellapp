package ecell.app.ecellteam;

public class Member implements Comparable<Member>{

        public String name;
        public String department;
        public Integer score;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDepartment() {
                return department;
        }

        public void setDepartment(String department) {
                this.department = department;
        }

        public Integer getScore() {
                return score;
        }

        public void setScore(int score) {
                this.score = score;
        }

        @Override
        public int compareTo(Member member) {
                return this.getScore().compareTo(member.getScore());
        }
}
