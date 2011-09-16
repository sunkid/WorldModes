#! /usr/bin/perl
use File::Find;

my $license =<<LICENSE_TXT;
/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid\@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid\@iminurnetz.com
 */
LICENSE_TXT

my @dirs = ();
my $dir = $0;
$dir =~ s/\/[^\/]*$//;
push @dirs, $dir . "/../../plugin/src/main/java/com/iminurnetz";
push @dirs, $dir . "/../src/main/java/com/iminurnetz";

find(\&wanted, @dirs);

my $inLicense;
my $lines;

sub wanted {
	local $file = $_;
	return if ($file !~ /\.java$/);
	open IN, "$file" or die "Cannot read from $file: $!\n";
	open OUT, ">$file.tmp" or die "Cannot write to $file.tmp: $!\n";
	print OUT $license;
	$inLicense = undef;
	$lines = 0;
	while (<IN>) {
		if ($inLicense && /^\s*\*/) {
			$inLicense .= $_;
			next;
		}

		if ($lines++ == 0) {
			$_ .= <IN>;
			if (/ \* LICENSING/) {
				$inLicense = $_;
				next;
			}
		}
		last if ($inLicense eq $license);
		$inLicense = undef;
		print OUT $_;
	}
	close IN;
	close OUT;
	if ($inLicense) {
		unlink "$file.tmp";
	} else {
		print STDERR "license added to $File::Find::name\n";
		rename "$file.tmp", $file or die "Cannot rename $file.tmp to $file\n";
	}
}
