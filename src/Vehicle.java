import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.LinkedList;
public class Vehicle{
	public double x, y;						//Pixel coords
	public double yaw;						//Radians
	public double left_track_speed;			//Pixels per sec
	public double right_track_speed;
	public double dist_to_checkpoint_x;		//Pixels
	public double dist_to_checkpoint_y;
	public int next_checkpoint_index;
	
	private NeuralNet neural_network;
	private Genome genome;
	private double width = 15;				//Pixels
	private double height = 10;
	
	// Constructor. Sets vars, creates a neural network, creates a genome based on that network,
	// then updates the network to have the same values as the genome.
	public Vehicle(double x, double y, double yaw){
		this.x = x;
		this.y = y;
		this.yaw = yaw;
		left_track_speed = 0;
		right_track_speed = 0;
		dist_to_checkpoint_x = 0;
		dist_to_checkpoint_y = 0;
		next_checkpoint_index = 0;
		neural_network = new NeuralNet();
		genome = new Genome(neural_network.getNumWeights());
		neural_network.setWeights(genome.getWeights());
	}
	
	// Constructor. Copy another vehicle.
	public Vehicle(Vehicle v){
		this.x = v.x;
		this.y = v.y;
		this.yaw = v.yaw;
		this.left_track_speed = v.left_track_speed;
		this.right_track_speed = v.right_track_speed;
		this.dist_to_checkpoint_x = v.dist_to_checkpoint_x;
		this.dist_to_checkpoint_y = v.dist_to_checkpoint_y;
		this.next_checkpoint_index = v.next_checkpoint_index;
		this.neural_network = new NeuralNet();
		this.genome = new Genome(v.getGenome());
		this.neural_network.setWeights(genome.getWeights());
		
	}
	// Updates 'left_track_speed' and 'right_track_speed' based on the neural network output.
	public void useNeuralNetwork(){
		LinkedList<Double> inputs = new LinkedList<Double>();
		inputs.add(x);
		inputs.add(y);
		inputs.add(yaw);
		inputs.add(dist_to_checkpoint_x);
		inputs.add(dist_to_checkpoint_y);
		double ang = Math.atan2(dist_to_checkpoint_y, dist_to_checkpoint_x);
		inputs.add(ang);
		//String input_str = String.format("x,y,yaw,distX,distY,ang = %.03f,%.03f,%.03f,%.03f,%.03f,%.03f",
		//			x,y,yaw,dist_to_checkpoint_x,dist_to_checkpoint_y,ang);
		//System.out.println(input_str);
		List<Double> outputs = neural_network.update(inputs);
		left_track_speed = outputs.get(0);
		right_track_speed = outputs.get(1);
		//String lspeed = String.format("%.02f", left_track_speed);
		//String rspeed = String.format("%.02f", right_track_speed);
		//System.out.println("New speed: " + lspeed + ", " + rspeed);
	}
	
	// Returns a shape, ready for displaying.
	public Shape getShape(){
		Rectangle2D.Double rectangle = new Rectangle2D.Double(x, y, width, height);
		AffineTransform transform = new AffineTransform();
		transform.rotate(yaw, x + width/2.0, y + height/2.0);
		Shape transformed = transform.createTransformedShape(rectangle);
		return transformed;
	}
	
	// Returns the internal color of the shape.
	public Color getColor(){
		Color col = Color.BLACK;
		switch (next_checkpoint_index){
			case(0):
				col = Color.GRAY;
				break;
			case(1):
				col = Color.GREEN;
				break;
			case(2):
				col = Color.RED;
				break;	
			case(3):
				col = Color.BLUE;
				break;	
			case(4):
				col = Color.ORANGE;
				break;
			case(5):
				col = Color.CYAN;
				break;	
			case(6):
				col = Color.YELLOW;
				break;	
		}
		return col;
	}
	
	public NeuralNet getNeuralNet(){
		return neural_network;
	}
	
	public Genome getGenome(){
		return genome;
	}
}