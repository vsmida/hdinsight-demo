package clustering

import com.twitter.scalding._

class Main( args: Args ) extends Job( args )
{

  val input = Tsv( args( "input" ) )
  val output = Tsv( args( "output" ) )

  val inputFields = ('city, 'review_count, 'name, 'neighborhoods, 'type, 'business_id, 'full_address, 'state, 'hours, 'longitude, 'stars, 'latitude, 'attributes, 'open, 'categories)
  input.read.write( output )

}
