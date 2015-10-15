// DiningPhiliphers.java (skeleton)
//
// - a classic synchronization problem
//
// Skeleton code derived from Dave Small's DiningPhiliphers.java v4.0

import java.util.Random;

//================================================ class DiningPhilosophers

class DiningPhilosophers
{
  public static void main( String[] arg )
  {
    new DiningPhilosophers( 10, 60000 );
  }

  private String[] name = { "Seneca", "Aristotle", "Epicurius", "Voltaire", 
			    "Kant", "Machiavelli", "Nietzsche", "Socrates", 
			    "Frege", "Hume" };

  private Philosopher[] thinker;
  private Chopstick[]   chopstick;

  public DiningPhilosophers( int numPhilosophers, int duration )
  {
    initialize( numPhilosophers );  // construct the philosophers & chopsticks
    startSimulation(); 
    sleep( duration );              // let simulation run for desired time
    shutdownPhilosophers();         // *gracefully* shut down the philosophers
    printResults();
  }

  private void initialize( int n )  // handles 2 to 10 philosophers
  {
    if ( n > 10 )
      n = 10;
    else if ( n < 2 )
      n = 2;

    thinker = new Philosopher[n];
    chopstick = new Chopstick[n];

    for ( int i = 0 ; i < n ; i++ )
      chopstick[i] = new Chopstick(i);

    for ( int i = 0 ; i < n ; i++ )
      thinker[i] = new Philosopher( name[i], chopstick[i], chopstick[(i+1)%n] );
  }

  private void startSimulation()
  {
    int n = thinker.length; // the number of philosophers

    System.out.print( "Our " + n + " philosophers (" );
    for ( int i = 0 ; i < (n-1) ; i++ )
      System.out.print( name[i] + ", " );
    System.out.println( "and " + name[n-1] +
			") have gather to think and dine" );

    System.out.println( "-----------------------------------------------");

    for ( int i = 0 ; i < n ; i++ )
      thinker[i].start();
  }

  private void sleep( int duration )
  {
    try
    {
      Thread.currentThread().sleep( duration );
    }
    catch ( InterruptedException e )
    {
      Thread.currentThread().interrupt();
    }
  }

  private void shutdownPhilosophers()
  {
    /********* YOUR CODE GOES HERE **********/
    for(int i = 0; i != thinker.length; ++i){
       thinker[i].interrupt();     
    }
  }

  private void printResults()
  {
    System.out.println( "-----------------------------------------------");

    int n = thinker.length; // the number of philosophers

    for ( int i = 0 ; i < n ; i++ )
      System.out.println( thinker[i] );

    System.out.flush();
  }
}

//================================================ class Philosopher

class Philosopher extends Thread
{
  static private Random random = new Random();

  private String name;
  private Chopstick leftStick;
  private Chopstick rightStick;

  private int eatingTime   = 0;
  private int thinkingTime = 0;
  private int countEat     = 0;
  private int countThink   = 0;

  public Philosopher( String name, Chopstick leftStick, Chopstick rightStick )
  {
    this.name = name;
    this.leftStick = leftStick;
    this.rightStick = rightStick;
  }

  public String toString()
  {
    return name + " ate " + countEat + " times (" +
      eatingTime + " ms) and pondered " + countThink + " times (" +
      thinkingTime + "ms)";
  }
  
  public void run()
  {
    /********* YOUR CODE GOES HERE **********/
    try{
        while(true){
          countThink++;
          thinkingTime += doAction( "think" );
          pickupChopsticks();
          countEat++;
          eatingTime += doAction( "eat" );
          putdownChopsticks();      
      }
    }
    catch(InterruptedException e){
        System.out.println(toString());
    }


    /********* YOUR CODE GOES HERE **********/
  }

  private int doAction( String act ) throws InterruptedException
  {
    int time = random.nextInt( 4000 ) + 1000 ;
    System.out.println( name + " is begining to " + act + " for " + time + 
			" milliseconds" );
    sleep( time );

    System.out.println( name + " is done " + act + "ing" );

    return time;
  }

  private void pickupChopsticks() throws InterruptedException
  {


      outter:
      while(true){
        synchronized(rightStick){
            System.out.println( name + " wants right " + rightStick ); 
             while(!rightStick.pickUp()){
                rightStick.wait();          
            }
              System.out.println( name + " has right " + rightStick );
              System.out.println( name + " wants left " + leftStick );              
              if(!leftStick.pickUp()){
                System.out.println( name + " was unable to get the left " + leftStick );
                System.out.println( name + " politely returned right " + rightStick ); 
                rightStick.putDown();   
                synchronized(leftStick){
                  leftStick.wait();       
                }    
              }
              else{
                System.out.println( name + " has both left " + leftStick +
                  " and right " + rightStick );    
                break outter;
              }   
        }     
      }
     
    
    
  }

  private void putdownChopsticks()
  {
    try{
      rightStick.putDown();
      System.out.println( name + " finished using right " + rightStick );
      leftStick.putDown();
      System.out.println( name + " finished using left " + leftStick ); 
    }
    catch(RuntimeException e){
      
    }
  }
}

//================================================ class Chopstick

class Chopstick
{
  private final int id;
  private Philosopher heldBy = null;

  public Chopstick( int id )
  {
    this.id = id;
  }

  public String toString()
  {
    return "chopstick (" + id + ")";
  }

  synchronized public boolean pickUp()
  {
    if(this.heldBy == null){
      // No one is holding the chopstick
      heldBy = (Philosopher) Thread.currentThread();
      return true;
    }
    else{
      return false;
    }
  }

  synchronized public void putDown()
  {
    Philosopher p = (Philosopher) Thread.currentThread();

    if( p != heldBy){
       throw new RuntimeException( "Exception: " + p + " attempted to put " +
          "down a chopstick he wasn't holding." );     
    }
    else{
      //letting go
      heldBy = null;
      this.notify();
    }

  }
}

