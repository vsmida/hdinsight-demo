package clustering

import com.twitter.scalding._

class Main( args: Args ) extends Job( args )
{
  val input = Tsv( args( "input" ), ('city, 'review_count, 'name, 'neighborhoods, 'type, 'business_id, 'full_address, 'state, 'hours, 'longitude, 'stars, 'latitude, 'attributes, 'open, 'categories))
  val output =Tsv( args( "output" ) )

  val pipe = input.read

    pipe.project('latitude, 'longitude)
      .write( output )



}
