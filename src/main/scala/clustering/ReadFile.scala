package clustering

import com.twitter.scalding._

class Main(args: Args) extends Job(args) {
  //params
  val input = Tsv(args("input"), ('city, 'review_count, 'name, 'neighborhoods, 'type, 'business_id, 'full_address, 'state, 'hours, 'longitude, 'stars, 'latitude, 'attributes, 'open, 'categories))
  val output = Tsv(args("output"))
  val K = args("clusters").toInt
  val convergeDist = args("convergeDist").toDouble
  val maxIter = args("maxIter").toInt

  val pipe = input.read


  //    val result = pipe.project('latitude, 'longitude)

  val result =
    pipe.map(('latitude, 'longitude) ->('latitude, 'longitude)) {
      data: (Double, Double) =>
        val (latitude, longitude) = data

        (latitude, longitude)
        
    }

  result.write(output)

  def distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float = {
    val earthRadius = 6371000; //meters
    val dLat = Math.toRadians(lat2-lat1)
    val dLng = Math.toRadians(lng2-lng1)
    val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLng/2) * Math.sin(dLng/2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    val dist = (earthRadius * c).toFloat

    return dist
  }
}
