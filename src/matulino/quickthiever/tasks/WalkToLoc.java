package matulino.quickthiever.tasks;

import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.ItemQuery;
import org.powerbot.script.rt4.TilePath;

	
public class WalkToLoc extends Task<ClientContext>{

	QuickThiever main;
	private int hpToEat;
	private final Component currHealth = ctx.widgets.widget(160).component(5);
	
	public WalkToLoc(ClientContext ctx, QuickThiever main) {
		super(ctx);
		this.main = main;
		
	}

	@Override
	public boolean activate() {
		ItemQuery<Item> food = ctx.inventory.select().action("Eat");
			

		return (food.size() >= 1)
				&& !ctx.npcs.select().name(main.thievTarg).nearest().poll().valid();
		
	}

	@Override
	public void execute() {
		main.status = "Walking to location...";
		TilePath path = ctx.movement.newTilePath(main.bankPath);
		path.traverse();
		
	}
	

}