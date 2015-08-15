import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math;

public class Environment {
	private static final double COLLISION_DIST = 10;
	private static final double LINEAR_SPEED_MULT = 2;
	private static final double ANGULAR_SPEED_MULT = 0.08;
	
	private List<Vehicle> vehicles;
	private List<Checkpoint> checkpoints;
	private Object mutex_lock;
	
	public double time;
	
	public Environment(List<Vehicle> vehicles, List<Checkpoint> checkpoints, Object mutex_lock){
		this.vehicles = vehicles;
		this.checkpoints = checkpoints;
		this.mutex_lock = mutex_lock;
		time = 0;
	}
	
	// Move vehicles to start point, and reset the checkpoint index.
	public void moveVehiclesToStart(){
		time = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			v.x = 100;
			v.y = 100;
			v.yaw = 0;
			v.next_checkpoint_index = 0;
			v.left_track_speed = 0;
			v.right_track_speed = 0;
			v.dist_to_checkpoint_x = 0;
			v.dist_to_checkpoint_y = 0;
			v.getGenome().setFitness(0);
			v.getGenome().setMaxFitness(0);
			
		}
		
	}
	// Assuming all vehicle speeds have been updated, this moves them and detects if the checkpoint
	// has been reached.
	public void update(double time_increment){
		time += time_increment;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			// Move each vehicle.
			double speed_diff = v.left_track_speed - v.right_track_speed;
			double speed_avg = (v.left_track_speed + v.right_track_speed) / 2.0;
			//System.out.println("Speed avg: " + speed_avg);
			//System.out.println("Move X: " + speed_avg * time_increment * Math.cos(v.yaw));
			v.x += LINEAR_SPEED_MULT * speed_avg * time_increment * Math.cos(v.yaw);
			v.y += LINEAR_SPEED_MULT * speed_avg * time_increment * Math.sin(v.yaw);
			v.yaw += ANGULAR_SPEED_MULT * speed_diff; 
			// Detect collisions with checkpoints.
			if (v.next_checkpoint_index < checkpoints.size()){
				Checkpoint cp = checkpoints.get(v.next_checkpoint_index);
				if (Math.abs(cp.x - v.x) <= COLLISION_DIST){
					if (Math.abs(cp.y - v.y) <= COLLISION_DIST){
						v.next_checkpoint_index ++;
					}
				}
			}
			// Update distance to checkpoints.
			if (v.next_checkpoint_index >= checkpoints.size()){
				v.dist_to_checkpoint_x = 0;
				v.dist_to_checkpoint_y = 0;
			} else {
				Checkpoint cp = checkpoints.get(v.next_checkpoint_index);
				v.dist_to_checkpoint_x = cp.x - v.x;
				v.dist_to_checkpoint_y = cp.y - v.y;
			}
			// Update fitness.
			double dist = Math.sqrt(v.dist_to_checkpoint_x*v.dist_to_checkpoint_x +
							v.dist_to_checkpoint_y*v.dist_to_checkpoint_y);
			v.getGenome().setFitness(v.next_checkpoint_index * 2000 - Math.min(800, dist) - time/4.0);
			
		}
	}
	
	public int getNumVehicles(){
		return vehicles.size();
	}
	
	public Vehicle getVehicle(int index){
		return vehicles.get(index);
	}	
	
	public int getNumCheckpoints(){
		return checkpoints.size();
	}
	public Checkpoint getCheckpoint(int index){
		return checkpoints.get(index);
	}
	
	public void setVehicles(List<Vehicle> vehicles){
		synchronized(mutex_lock){
			this.vehicles = vehicles;
		}
	}
}