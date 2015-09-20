package game.mobs;

public class JerryTheIceCreamMan extends Mob {

	public JerryTheIceCreamMan(int id, double hp) {
		super("/Jerry_The_Icecream_Man.png", "/Jerry_The_Icecream_Man_Hurt.png", "/Jerry_The_Icecream_Man_Death.png",
				4, 1, 4, 10, id, hp);
	}

	@Override
	public int getType() {
		return 1;
	}

}
