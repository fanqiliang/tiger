package elaborator;

import java.util.LinkedList;

public class ElaboratorVisitor implements ast.Visitor {
	public ClassTable classTable; // symbol table for class
	public MethodTable methodTable; // symbol table for each method
	public String currentClass; // the class name being elaborated
	public ast.type.T type; // type of the expression being elaborated

	public ElaboratorVisitor() {
		this.classTable = new ClassTable();
		this.methodTable = new MethodTable();
		this.currentClass = null;
		this.type = null;
	}

	private void error(String err) {
		System.out.println("type mismatch " + err);
		System.exit(1);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		e.left.accept(this);
		if (!this.type.toString().equals("@int"))
			error("Add left is error");
		e.right.accept(this);
		if (!this.type.toString().equals("@int"))
			error("Add right is error");
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("And left is error");
		e.right.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("Add right is error");
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		e.array.accept(this);
		if (!this.type.toString().equals("@int[]"))
			error("ArraySelect array is error");
		e.index.accept(this);
		if (!this.type.toString().equals("@int"))
			error("ArraySelect index is error");
		return;
	}

	@Override
	public void visit(ast.exp.Call e) {
		ast.type.T leftty;
		ast.type.Class ty = null;

		e.exp.accept(this);
		leftty = this.type;
		if (leftty instanceof ast.type.Class) {
			ty = (ast.type.Class) leftty;
			e.type = ty.id;
		} else
			error("Call leftty is error,leftty's type is " + leftty);
		MethodType mty = this.classTable.getm(ty.id, e.id);
		java.util.LinkedList<ast.type.T> deArgsType = new LinkedList<ast.type.T>();
		for (ast.dec.T decs : mty.argsType) {
			ast.dec.Dec dec = (ast.dec.Dec)decs;
			deArgsType.add(dec.type);
		}
		java.util.LinkedList<ast.type.T> argsty = new java.util.LinkedList<ast.type.T>();
		for (ast.exp.T a : e.args) {
			a.accept(this);
			argsty.addLast(this.type);
		}
		if (deArgsType.size() != argsty.size())
			error("Call argsType is error");
		for (int i = 0; i < argsty.size(); i++) {
			ast.dec.Dec dec = (ast.dec.Dec) mty.argsType.get(i);
			if (dec.type.toString().equals(argsty.get(i).toString()))
				;
			else {
				String ancestor = argsty.get(i).toString();
				for (;;) {
					// find if dec.type.toString() is ancestor of
					// argsty.get(i).toString()
					ClassBinding cb = this.classTable.get(ancestor);
					if (cb.extendss == null)
						error("in Call, dec type is " + dec.type.toString()
								+ ", real is " + argsty.get(i).toString());
					else {
						if (cb.extendss.equals(dec.type.toString()))
							;// detected extends
						else {
							ancestor = cb.extendss;
							continue;
						}
					}
					break;
				}
			}
		}
		this.type = mty.retType;
		e.at = deArgsType;
		e.rt = this.type;
		return;
	}

	@Override
	public void visit(ast.exp.False e) {
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.Id e) {
		// first look up the id in method table
		ast.type.T type = this.methodTable.get(e.id);
		// if search failed, then s.id must be a class field.
		if (type == null) {
			type = this.classTable.get(this.currentClass, e.id);
			// mark this id as a field id, this fact will be
			// useful in later phase.
			e.isField = true;
		}
		if (type == null)
			error("Id has no type and error");
		this.type = type;
		// record this type on this node for future use.
		e.type = type;
		return;
	}

	@Override
	public void visit(ast.exp.Length e) {
		e.array.accept(this);
		if (!this.type.toString().equals("@int[]"))
			error("Length is error");
		return;
	}

	@Override
	public void visit(ast.exp.Lt e) {
		e.left.accept(this);
		ast.type.T ty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(ty.toString()))
			error("Lt is error,left type is " + ty.toString()
					+ " and right type is " + this.type.toString());
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		e.exp.accept(this);
		if (!this.type.toString().equals("@int"))
			error("NewIntArray is error, the current type is "
					+ this.type.toString());
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.type = new ast.type.Class(e.id);
		return;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("Not is error");
		return;
	}

	@Override
	public void visit(ast.exp.Num e) {
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			error("Sub is error,current the left type is " + leftty.toString()
					+ " ,the right type is " + this.type.toString());
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.This e) {
		this.type = new ast.type.Class(this.currentClass);
		return;
	}

	@Override
	public void visit(ast.exp.Times e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			error("Sub is error,current the left type is " + leftty.toString()
					+ " ,the right type is " + this.type.toString());
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.True e) {
		this.type = new ast.type.Boolean();
		return;
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		// first look up the id in method table
		ast.type.T type = this.methodTable.get(s.id);
		// if search failed, then s.id must
		if (type == null)
			type = this.classTable.get(this.currentClass, s.id);
		if (type == null)
			error("Assign is error");
		s.exp.accept(this);
		s.type = type;
		this.type.toString().equals(type.toString());
		return;
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		ast.type.T type = this.methodTable.get(s.id);
		if (type == null)
			type = this.classTable.get(this.currentClass, s.id);
		if (type == null)
			error("AssignArray is error 1");
		s.exp.accept(this);
		if (!this.type.toString().equals("@int"))
			error("AssignArray is error 2");
		s.index.accept(this);
		if (!this.type.toString().equals("@int"))
			error("AssignArray is error 3,current type is "
					+ this.type.toString() + ", type is " + type);
		return;
	}

	@Override
	public void visit(ast.stm.Block s) {
		for (ast.stm.T stm : s.stms)
			stm.accept(this);
		return;
	}

	@Override
	public void visit(ast.stm.If s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("If is error, current type is " + this.type.toString());
		s.thenn.accept(this);
		s.elsee.accept(this);
		return;
	}

	@Override
	public void visit(ast.stm.Print s) {
		s.exp.accept(this);
		if (!this.type.toString().equals("@int"))
			error("Print is error, current type is " + this.type.toString());
		return;
	}

	@Override
	public void visit(ast.stm.While s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("While is error, current type is " + this.type.toString());
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		t.accept(this);
		if (!this.type.toString().equals("@boolean"))
			error("Boolean is error, current type is " + this.type.toString());
		return;
	}

	@Override
	public void visit(ast.type.Class t) {
		t.accept(this);
		this.type = new ast.type.Class(t.id);
		return;
	}

	@Override
	public void visit(ast.type.Int t) {
		t.accept(this);
		if (!this.type.toString().equals("@int"))
			error("Int is error, current type is " + this.type.toString());
		return;
	}

	@Override
	public void visit(ast.type.IntArray t) {
		t.accept(this);
		if (!this.type.toString().equals("@int[]"))
			error("IntArray is error, current type is " + this.type.toString());
		return;
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		d.type.accept(this);
		ast.type.T type = this.type;
		this.type = new ast.type.Class(d.id);
		if (!this.type.toString().equals(type))
			error("Dec is error,this.type is " + this.type + ", type is "
					+ type);
		return;
	}

	// method
	@Override
	public void visit(ast.method.Method m) {
		this.methodTable = new MethodTable();// we should use new methodtables
		// construct the method table
		this.methodTable.put(m.formals, m.locals);

		if (control.Control.elabMethodTable)
			this.methodTable.dump(m.id);

		for (ast.stm.T s : m.stms)
			s.accept(this);
		m.retExp.accept(this);
		return;
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.currentClass = c.id;

		for (ast.method.T m : c.methods) {
			m.accept(this);
		}
		return;
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		this.currentClass = c.id;
		// "main" has an argument "arg" of type "String[]", but
		// one has no chance to use it. So it's safe to skip it...

		c.stm.accept(this);
		return;
	}

	// ////////////////////////////////////////////////////////
	// step 1: build class table
	// class table for Main class
	private void buildMainClass(ast.mainClass.MainClass main) {
		this.classTable.put(main.id, new ClassBinding(null));
	}

	// class table for normal classes
	private void buildClass(ast.classs.Class c) {
		this.classTable.put(c.id, new ClassBinding(c.extendss));
		for (ast.dec.T dec : c.decs) {
			ast.dec.Dec d = (ast.dec.Dec) dec;
			this.classTable.put(c.id, d.id, d.type);
		}
		for (ast.method.T method : c.methods) {
			ast.method.Method m = (ast.method.Method) method;
			this.classTable.put(c.id, m.id,
					new MethodType(m.retType, m.formals));
		}
	}

	// step 1: end
	// ///////////////////////////////////////////////////

	// program
	@Override
	public void visit(ast.program.Program p) {
		// ////////////////////////////////////////////////
		// step 1: build a symbol table for class (the class table)
		// a class table is a mapping from class names to class bindings
		// classTable: className -> ClassBinding{extends, fields, methods}
		buildMainClass((ast.mainClass.MainClass) p.mainClass);
		for (ast.classs.T c : p.classes) {
			buildClass((ast.classs.Class) c);
		}

		// we can double check that the class table is OK!
		if (control.Control.elabClassTable) {
			this.classTable.dump();
		}

		// ////////////////////////////////////////////////
		// step 2: elaborate each class in turn, under the class table
		// built above.
		p.mainClass.accept(this);
		for (ast.classs.T c : p.classes) {
			c.accept(this);
		}

	}
}
