package inter;//文件 While.java
import symbols.*;
public class For extends Stmt {
    Stmt enter;
    Expr condition;
    Stmt update;
    Stmt body;
    public For() {
        this.enter = null;
        this.condition = null;
        this.update = null;
        this.body = null;
    }

    public void init(Stmt enter,Expr condition,Stmt update,Stmt body) {
        this.enter = enter;
        this.condition = condition;
        this.update = update;
        this.body = body;

        if (this.condition.type != Type.Bool) condition.error("boolean required in while");
    }

    public void gen(int b, int a) {
        after = a;//保存标号a
        int body_label = newlabel();
        int condition_label = newlabel();
        int update_label = newlabel();
        this.enter.gen(b,body_label);
        emitlabel(body_label);
        body.gen(body_label,condition_label);
        emitlabel(condition_label);
        this.condition.jumping(0, a);
        emitlabel(update_label);
        this.update.gen(update_label,a);
        emit("goto L" + body_label);
    }
}